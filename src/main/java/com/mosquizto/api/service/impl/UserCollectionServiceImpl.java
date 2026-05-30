package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.ErrorCode;
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
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.AccessStatus;
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

        if (!collection.isOwnedBy(usernameOwner)) {
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
                .orElseGet(() -> UserCollection.createShareInvite(sharedUser, collection, shareCollectionRequest.getRole()));

        userCollection.changeRole(shareCollectionRequest.getRole());
        userCollection.markPending();
        this.userCollectionRepository.save(userCollection);
    }

    @Override
    public List<MemberResponse> getAllMembersCollection(Integer collectionId) {
        User currentUser = this.currentUserProvider.getCurrentUser();

        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.isOwnedBy(currentUser)) {
            UserCollection membership = this.userCollectionRepository
                    .findByUserIdAndCollectionId(currentUser.getId(), collectionId)
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

        Collection collection = this.collectionRepository.findById(collectionId)
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
        String username = this.currentUserProvider.getCurrentUsername();
        User user = this.userService.getById(userId);

        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.isOwnedBy(username) || collection.isOwnedBy(user)) {
            throw new AccessDeniedException("You cannot remove");
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
        UserCollectionId id = new UserCollectionId(userId, collectionId);
        UserCollection request = userCollectionRepository.findById(id)
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
        userCollectionRepository.findByUserIdAndCollectionId(userId, collectionId)
                .ifPresent(userCollection -> {
                    userCollection.touchLastOpenedAt(new Date());
                    userCollectionRepository.save(userCollection);
                });
    }
}
