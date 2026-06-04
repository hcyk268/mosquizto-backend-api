package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.dto.response.ShareCollectionResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.ErrorCode;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.mapper.UserCollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.MailService;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserCollectionServiceImpl implements UserCollectionService {

    private final CurrentUserProvider currentUserProvider;
    private final UserService userService;
    private final CollectionRepository collectionRepository;
    private final UserCollectionRepository userCollectionRepository;
    private final UserRepository userRepository ;
    private final UserCollectionMapper userCollectionMapper;
    private final MailService mailService ;
    @Override
    @Transactional
    public void shareCollection(Integer collectionId, ShareCollectionRequest shareCollectionRequest) {
        User inviter = this.currentUserProvider.getCurrentUser();
        String usernameOwner = inviter.getUsername();
        String recipientName = shareCollectionRequest.getUsername() ;
        User targetUser = userRepository.findActiveByUsername(recipientName).orElseThrow(() ->
                new ResourceNotFoundException(recipientName + "does not exits"));

        Collection collection = this.collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.isOwnedBy(usernameOwner) &&  shareCollectionRequest.getRole() == CollectionRole.EDITOR) {
            throw new AccessDeniedException("You do not have permission to share this collection");
        }

        if (usernameOwner.equals(shareCollectionRequest.getUsername())) {
            throw new InvalidDataException("You cannot share this collection to yourself");
        }

        User sharedUser = this.userService.getByUsername(shareCollectionRequest.getUsername());
        UserCollectionId id = UserCollectionId.builder()
                .userId(sharedUser.getId())
                .collectionId(collection.getId())
                .build();

        UserCollection userCollection = this.userCollectionRepository.findById(id)
                .orElseGet(null);

        if (userCollection != null) {
            if (userCollection.isActive()) {
                throw new ConflictException("User has already joined this collection");
            }
            userCollection.restore();
            userCollection.setInvitedBy(inviter);
            userCollection.changeRole(shareCollectionRequest.getRole());
            userCollection.markPending();

            this.userCollectionRepository.save(userCollection);
        } else {
            userCollection = UserCollection.createShareInvite(sharedUser, collection, shareCollectionRequest.getRole(), inviter);
            this.userCollectionRepository.save(userCollection);
        }
        // gửi mail
        mailService.sendCollectionShareInvite(targetUser.getEmail(), targetUser.getUsername(),inviter.getUsername()
                ,collection.getTitle(),shareCollectionRequest.getRole().name());
    }

    @Override
    public List<MemberResponse> getAllMembersCollection(Integer collectionId) {
        User currentUser = this.currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.isOwnedBy(currentUser)) {
            UserCollection membership = this.userCollectionRepository
                    .findActiveByUserIdAndCollectionId(currentUser.getId(), collectionId)
                    .orElse(null);

            if (membership == null || !membership.canView()) {
                throw new AccessDeniedException("You can not access members list");
            }
        }

        LinkedHashMap<Long, MemberResponse> members = new LinkedHashMap<>();
        members.put(collection.getCreatedBy().getId(), this.userCollectionMapper.toOwnerMemberResponse(collection.getCreatedBy()));

        this.userCollectionRepository.findAllActiveMembersByCollectionId(collectionId)
                .forEach(userCollection -> members.putIfAbsent(
                        userCollection.getUser().getId(), this.userCollectionMapper.toMemberResponse(userCollection)));

        return new ArrayList<>(members.values());
    }

    @Override
    @Transactional
    public void joinCollection(Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.isPublic()) {
            throw new AccessDeniedException("Collection is private");
        }

        if (collection.isOwnedBy(user)) {
            throw new ConflictException(ErrorCode.ALREADY_JOINED, "You joined before");
        }

        UserCollectionId id = UserCollectionId.builder()
                .collectionId(collectionId)
                .userId(user.getId())
                .build();

        UserCollection existingMembership = this.userCollectionRepository.findById(id).orElse(null);
        if (existingMembership != null) {
            if (existingMembership.getDeletedAt() != null) {
                existingMembership.restore();
                existingMembership.changeRole(CollectionRole.VIEWER);
                existingMembership.markPending();
                existingMembership.touchLastOpenedAt(new Date());
                this.userCollectionRepository.save(existingMembership);
                return;
            }

            if (existingMembership.isActive()) {
                throw new ConflictException(ErrorCode.ALREADY_JOINED, "You have already joined this collection");
            }

            if (existingMembership.isPending()) {
                throw new ConflictException(ErrorCode.JOIN_REQUEST_PENDING, "Your join request is pending");
            }

            if (existingMembership.isDenied()) {
                throw new AccessDeniedException(ErrorCode.JOIN_REQUEST_DENIED, "You are denied");
            }
        }

        UserCollection userCollection = UserCollection.requestJoin(user, collection);
        this.userCollectionRepository.save(userCollection);
    }

    @Override
    @Transactional
    public void deleteCollectionMember(Integer collectionId, Long userId) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        String username = currentUser.getUsername();
        User user = this.userService.getById(userId);

        Collection collection = this.collectionRepository.findActiveById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.isOwnedBy(username) || collection.isOwnedBy(user)) {
            throw new AccessDeniedException("You cannot remove");
        }

        UserCollectionId idDelete = UserCollectionId.builder()
                .collectionId(collectionId)
                .userId(userId)
                .build();

        UserCollection membership = this.userCollectionRepository.findActiveById(idDelete).orElse(null);
        if (membership != null) {
            membership.delete(currentUser);
            this.userCollectionRepository.save(membership);
        }
    }

    @Override
    @Transactional
    public void approveJoinRequest(Integer collectionId, Long userId, AccessStatus status) {
        String ownerUsername = currentUserProvider.getCurrentUsername();
        UserCollectionId id = new UserCollectionId(userId, collectionId);
        UserCollection request = userCollectionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getCollection().isOwnedBy(ownerUsername)) {
            throw new AccessDeniedException("Only owner can approve requests");
        }

        if (status == AccessStatus.DENIED) {
            request.deny();
        } else {
            request.approve();
        }

        userCollectionRepository.save(request);
    }

    @Override
    @Transactional
    @Async
    public void updateLastOpenedAt(Long userId, Integer collectionId) {
        // Tìm kiếm xem bản ghi UserCollection đã tồn tại chưa
        UserCollection existingRecord = userCollectionRepository
                .findActiveByUserIdAndCollectionId(userId, collectionId)
                .orElse(null);

        if (existingRecord != null) {
            // Đã tồn tại -> Chỉ cập nhật thời gian mở cuối (lastOpenedAt)
            existingRecord.touchLastOpenedAt(new Date());
            userCollectionRepository.save(existingRecord);
        } else {
            // Chưa tồn tại -> Tìm User và Collection tương ứng để tạo mới
            // Sử dụng repository để lấy đối tượng User và Collection (cần thiết để liên kết khoá ngoại)
            User user = userRepository.findById(userId).orElse(null);
            Collection collection = collectionRepository.findById(collectionId).orElse(null);

            // Đảm bảo User và Collection hợp lệ trước khi lưu
            if (user != null && collection != null) {
                UserCollectionId id = UserCollectionId.builder()
                        .userId(userId)
                        .collectionId(collectionId)
                        .build();

                UserCollection newUserCollection = UserCollection.builder()
                        .id(id)
                        .user(user)
                        .collection(collection)
                        .role(CollectionRole.VIEWER)            // Role mặc định là VIEWER
                        .accessStatus(AccessStatus.ENABLE)      // Trạng thái ENABLE
                        .lastOpenedAt(new Date())
                        .build();

                userCollectionRepository.save(newUserCollection);
            }
        }
    }
    @Override
    @Transactional
    public void removeRecentOpenedCollection(Integer collectionId) {
        Long userId = this.currentUserProvider.getCurrentUser().getId();
        UserCollection userCollection = userCollectionRepository
                .findActiveByUserIdAndCollectionId(userId, collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection is not found or you don't have permission"));
        userCollection.setLastOpenedAt(null);
        userCollectionRepository.save(userCollection);
    }

    @Override
    public List<ShareCollectionResponse> getMyPendingInvitations() {
        Long userId = this.currentUserProvider.getCurrentUser().getId();
        List<UserCollection> pendingList = userCollectionRepository.findPendingInvitationsByUserId(userId);

        return pendingList.stream().map(uc -> {
            Collection c = uc.getCollection();
            User inviter = uc.getInvitedBy();

            ShareCollectionResponse res = new ShareCollectionResponse();
            res.setCollectionId(c.getId());
            res.setTitle(c.getTitle());
            res.setDescription(c.getDescription());
            res.setInviteAt(uc.getCreatedAt());
            res.setCollectionRole(uc.getRole());
            res.setAccessStatus(uc.getAccessStatus());

            if (inviter != null) {
                res.setInviterId(inviter.getId());
                res.setInviterUsername(inviter.getUsername());
            } else {
                res.setInviterId(c.getCreatedBy().getId());
                res.setInviterUsername(c.getCreatedBy().getUsername());
            }

            return res;
        }).toList();
    }

    @Override
    @Transactional
    public void respondToShareInvite(Integer collectionId, AccessStatus status) {
        Long userId = this.currentUserProvider.getCurrentUser().getId();
        UserCollectionId id = new UserCollectionId(userId, collectionId);

        UserCollection request = userCollectionRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        // Chỉ cho phép xử lý nếu trạng thái đang là PENDING
        if (!request.isPending()) {
            throw new InvalidDataException("This invitation is no longer pending");
        }

        // Người được mời ấn Accept (ENABLE) hoặc Deny (DENIED)
        if (status == AccessStatus.DENIED) {
            request.deny();
        } else if (status == AccessStatus.ENABLE) {
            request.approve();
        } else {
            throw new InvalidDataException("Invalid status");
        }

        userCollectionRepository.save(request);
    }

    @Override
    public CollectionRole getRoleOfCollection(Integer collectionId) { // Bỏ inviterId đi cũng được
        Long currentUserId = currentUserProvider.getCurrentUser().getId();

        return this.userCollectionRepository.getCollectionRole(collectionId, currentUserId)
                .orElse(CollectionRole.UNKNOW); // Hoặc ném Exception nếu thích
    }
}
