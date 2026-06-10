package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.StarredCollectionItemResponse;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionItemMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.UserCollectionItemStar;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.UserCollectionItemStarRepository;
import com.mosquizto.api.service.CollectionItemStarService;
import com.mosquizto.api.service.CollectionMembershipResolver;
import com.mosquizto.api.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CollectionItemStarServiceImpl implements CollectionItemStarService {

    private final CurrentUserProvider currentUserProvider;
    private final CollectionItemRepository collectionItemRepository;
    private final UserCollectionItemStarRepository starRepository;
    private final CollectionItemMapper collectionItemMapper;
    private final CollectionMembershipResolver membershipResolver;

    @Override
    @Transactional
    public StarredCollectionItemResponse starItem(Integer itemId) {
        User user = this.currentUserProvider.getCurrentUser();
        CollectionItem item = getAccessibleItem(user, itemId);

        UserCollectionItemStar star = this.starRepository
                .findActiveByUserIdAndCollectionItemId(user.getId(), itemId)
                .orElseGet(() -> UserCollectionItemStar.create(user, item));

        return collectionItemMapper.toResponse(this.starRepository.save(star));
    }

    @Override
    @Transactional
    public void unstarItem(Integer itemId) {
        User user = this.currentUserProvider.getCurrentUser();
        getAccessibleItem(user, itemId);

        if (!this.starRepository.existsActiveByUserIdAndCollectionItemId(user.getId(), itemId)) {
            return;
        }

        this.starRepository.deleteByUserIdAndCollectionItemId(user.getId(), itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StarredCollectionItemResponse> getMyStarredItems() {
        User user = this.currentUserProvider.getCurrentUser();
        return this.starRepository.findAllActiveByUserId(user.getId())
                .stream()
                .filter(star -> canView(user, star.getCollectionItem().getCollection()))
                .map(collectionItemMapper::toResponse)
                .toList();
    }

    private CollectionItem getAccessibleItem(User user, Integer itemId) {
        CollectionItem item = this.collectionItemRepository.findActiveById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection item not found"));

        membershipResolver.requireCanView(item.getCollection(), user);
        return item;
    }

    private boolean canView(User user, Collection collection) {
        UserCollection membership = membershipResolver.getMembership(user.getId(), collection.getId());
        return collection.canViewContent(user, membership);
    }
}
