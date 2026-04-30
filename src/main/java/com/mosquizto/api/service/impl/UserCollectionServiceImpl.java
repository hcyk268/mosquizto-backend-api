package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.UserCollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.service.UserCollectionService;
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
    private final UserCollectionMapper userCollectionMapper;

    @Override
    @Transactional
    public void shareCollection(Integer collectionId, ShareCollectionRequest shareCollectionRequest) {
        String usernameOwner = this.currentUserProvider.getCurrentUsername();
        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getCreatedBy().getUsername().equals(usernameOwner)) {
            throw new InvalidDataException("You do not have permission to share this collection");
        }

        if (usernameOwner.equals(shareCollectionRequest.getUsername())) {
            throw new InvalidDataException("You cannot share this collection to yourself");
        }

        if (shareCollectionRequest.getRole() == CollectionRole.OWNER) {
            throw new InvalidDataException("Role OWNER is reserved for the collection creator");
        }

        User sharedUser = this.userService.getByUsername(shareCollectionRequest.getUsername());
        UserCollectionId id = UserCollectionId.builder()
                .userId(sharedUser.getId())
                .collectionId(collection.getId())
                .build();

        UserCollection userCollection = this.userCollectionRepository.findById(id)
                .orElseGet(() -> UserCollection.builder()
                        .id(id).user(sharedUser).collection(collection)
                        .accessStatus(AccessStatus.PENDING) // Đợi người kia đồng ý
                        .build());

        userCollection.setRole(shareCollectionRequest.getRole());
        this.userCollectionRepository.save(userCollection);
    }

    @Override
    public List<MemberResponse> getAllMembersCollection(Integer collectionId) {
        User currentUser = this.currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        boolean isOwner = collection.getCreatedBy().getId().equals(currentUser.getId());
        if (!isOwner) {
            UserCollectionId id = UserCollectionId.builder()
                    .userId(currentUser.getId())
                    .collectionId(collectionId)
                    .build();

            if (!this.userCollectionRepository.existsById(id)) {
                throw new InvalidDataException("You can not access members list");
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
    public void joinCollection(Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();
        String username = user.getUsername();

        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getVisibility()) {
            throw new InvalidDataException("Collection is private");
        }

        if (collection.getCreatedBy().getUsername().equals(username)) {
            throw new InvalidDataException("You joined before");
        }

        UserCollectionId id = UserCollectionId.builder()
                .collectionId(collectionId)
                .userId(user.getId())
                .build();

        boolean isJoinedCollection = this.userCollectionRepository.existsById(id);

        if (!isJoinedCollection) {
            CollectionRole collectionRole = (collection.getCreatedBy().getId().equals(user.getId())) ? CollectionRole.OWNER : CollectionRole.VIEWER ;
            UserCollection userCollection = UserCollection.builder()
                    .id(id)
                    .user(user)
                    .collection(collection)
                    .role(CollectionRole.VIEWER)
                    .accessStatus(AccessStatus.PENDING)
                    .role(collectionRole)
                    .lastOpenedAt(new Date())
                    .build();

            this.userCollectionRepository.save(userCollection);
        }
    }

    @Override
    @Transactional
    public void deleteCollectionMember(Integer collectionId, Long userId) {
        String username = this.currentUserProvider.getCurrentUsername();

        User user = this.userService.getById(userId);

        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getCreatedBy().getUsername().equals(username)
            || collection.getCreatedBy().getUsername().equals(user.getUsername())) {
            throw new InvalidDataException("You cannot remove");
        }

        UserCollectionId idDelete = UserCollectionId.builder()
                .collectionId(collectionId)
                .userId(userId)
                .build();

        if (this.userCollectionRepository.existsById(idDelete)) {
            this.userCollectionRepository.deleteById(idDelete);
        }
    }

    @Override
    @Transactional
    public void approveJoinRequest(Integer collectionId, Long userId, AccessStatus status) {
        String ownerUsername = currentUserProvider.getCurrentUsername();
        // Tìm record PENDING
        UserCollectionId id = new UserCollectionId(userId, collectionId);
        UserCollection request = userCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // Kiểm tra xem người đang gọi có phải là chủ sở hữu collection không
        if (!request.getCollection().getCreatedBy().getUsername().equals(ownerUsername)) {
            throw new InvalidDataException("Only owner can approve requests");
        }

        if (status == AccessStatus.DENIED) {
            // Nếu từ chối, có thể xóa luôn record hoặc để status DENIED tùy bạn
            userCollectionRepository.delete(request);
        } else {
            request.setAccessStatus(AccessStatus.ENABLE);
            userCollectionRepository.save(request);
        }
    }

    @Override
    @Transactional
    @Async
    public void updateLastOpenedAt(Long userId , Integer collectionId) {
        userCollectionRepository.findByUserIdAndCollectionId(userId, collectionId)
                .ifPresent(userCollection -> {
                    userCollection.setLastOpenedAt(new Date());
                    userCollectionRepository.save(userCollection);
                });
    }

    @Override
    public List<UserCollection> getRecentOpenedCollection() {
        Long userId = currentUserProvider.getCurrentUser().getId();
        return userCollectionRepository.findTop10ByUserIdOrderByLastOpenedAtDesc(userId);
    }
}
