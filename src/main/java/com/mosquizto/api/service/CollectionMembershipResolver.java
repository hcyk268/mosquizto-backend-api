package com.mosquizto.api.service;

import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.repository.UserCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CollectionMembershipResolver {

    private final UserCollectionRepository userCollectionRepository;


    public UserCollection getMembership(Long userId, Integer collectionId) {
        return userCollectionRepository
                .findByUserIdAndCollectionId(userId, collectionId)
                .orElse(null);
    }

    public void requireCanView(Collection collection, User user) {
        UserCollection membership = getMembership(user.getId(), collection.getId());
        if (!collection.canView(user, membership)) {
            throw new AccessDeniedException("You do not have permission to view this collection");
        }
    }

    public void requireCanEdit(Collection collection, User user) {
        UserCollection membership = getMembership(user.getId(), collection.getId());
        if (!collection.canEdit(membership)) {
            throw new AccessDeniedException("Only editor and owner can edit this collection");
        }
    }

    public void requireCanDelete(Collection collection, User user) {
        UserCollection membership = getMembership(user.getId(), collection.getId());
        if (!collection.canDelete(membership)) {
            throw new AccessDeniedException("Only the owner can delete this collection");
        }
    }
}
