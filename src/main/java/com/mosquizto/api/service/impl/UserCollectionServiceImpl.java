package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.key.UserCollectionId;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionReposotory;
import com.mosquizto.api.service.JwtService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.util.CollectionRole;
import com.mosquizto.api.util.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserCollectionServiceImpl implements UserCollectionService {

    private final JwtService jwtService;
    private final UserService userService;
    private final CollectionRepository collectionRepository;
    private final UserCollectionReposotory userCollectionReposotory;

    @Override
    public void shareCollection(String token, Integer collectionId, ShareCollectionRequest shareCollectionRequest) {
        String usernameOwner = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
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

        UserCollection userCollection = this.userCollectionReposotory.findById(id)
                .orElseGet(() -> UserCollection.builder()
                        .id(id)
                        .user(sharedUser)
                        .collection(collection)
                        .build());

        userCollection.setRole(shareCollectionRequest.getRole());
        this.userCollectionReposotory.save(userCollection);
    }

    @Override
    public List<MemberResponse> getAllMembersCollection(String token, Integer collectionId) {
        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        User currentUser = this.userService.getByUsername(username);

        Collection collection = this.collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        boolean isOwner = collection.getCreatedBy().getId().equals(currentUser.getId());
        if (!isOwner) {
            UserCollectionId id = UserCollectionId.builder()
                    .userId(currentUser.getId())
                    .collectionId(collectionId)
                    .build();

            if (!this.userCollectionReposotory.existsById(id)) {
                throw new InvalidDataException("You can not access members list");
            }
        }

        LinkedHashMap<Long, MemberResponse> members = new LinkedHashMap<>();

        members.put(collection.getCreatedBy().getId(), MemberResponse.builder()
                .username(collection.getCreatedBy().getUsername())
                .fullname(collection.getCreatedBy().getFullName())
                .role(CollectionRole.OWNER)
                .build());

        this.userCollectionReposotory.findAllMembersByCollectionId(collectionId)
                .forEach(userCollection -> members.putIfAbsent(
                        userCollection.getUser().getId(), MemberResponse.builder()
                            .username(userCollection.getUser().getUsername())
                            .fullname(userCollection.getUser().getFullName())
                            .role(userCollection.getRole())
                            .build()));

        return new ArrayList<>(members.values());
    }

    @Override
    public void joinCollection(String token, Integer collectionId) {
        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
        User user = this.userService.getByUsername(username);

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

        boolean isJoinedCollection = this.userCollectionReposotory.existsById(id);

        if (!isJoinedCollection) {
            UserCollection userCollection = UserCollection.builder()
                    .id(id)
                    .user(user)
                    .collection(collection)
                    .role(CollectionRole.VIEWER)
                    .build();

            this.userCollectionReposotory.save(userCollection);
        }
    }

    @Override
    @Transactional
    public void deleteCollectionMember(String token, Integer collectionId, Long userId) {
        String username = this.jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);

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

        if (this.userCollectionReposotory.existsById(idDelete)) {
            this.userCollectionReposotory.deleteById(idDelete);
        }
    }
}
