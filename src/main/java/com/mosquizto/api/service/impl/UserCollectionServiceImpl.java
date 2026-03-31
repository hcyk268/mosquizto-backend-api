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
import com.mosquizto.api.util.CollectionRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                        .id(id)
                        .user(sharedUser)
                        .collection(collection)
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

        this.userCollectionRepository.findAllMembersByCollectionId(collectionId)
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
            UserCollection userCollection = UserCollection.builder()
                    .id(id)
                    .user(user)
                    .collection(collection)
                    .role(CollectionRole.VIEWER)
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
}
