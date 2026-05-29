package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.StarredCollectionItemResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.CollectionItemMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.model.UserCollectionItemStar;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.UserCollectionItemStarRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.CollectionItemStarService;
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
    private final UserCollectionRepository userCollectionRepository;
    private final UserCollectionItemStarRepository starRepository;
    private final CollectionItemMapper collectionItemMapper;

    @Override
    @Transactional
    public StarredCollectionItemResponse starItem(Integer itemId) {
        User user = this.currentUserProvider.getCurrentUser();
        CollectionItem item = getAccessibleItem(user, itemId);

        UserCollectionItemStar star = this.starRepository
                .findByUserIdAndCollectionItemId(user.getId(), itemId)
                .orElseGet(() -> UserCollectionItemStar.create(user, item));

        return collectionItemMapper.toResponse(this.starRepository.save(star));
    }

    @Override
    @Transactional
    public void unstarItem(Integer itemId) {
        User user = this.currentUserProvider.getCurrentUser();
        getAccessibleItem(user, itemId);

        if (!this.starRepository.existsByUserIdAndCollectionItemId(user.getId(), itemId)) {
            return;
        }

        this.starRepository.deleteByUserIdAndCollectionItemId(user.getId(), itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StarredCollectionItemResponse> getMyStarredItems() {
        User user = this.currentUserProvider.getCurrentUser();
        return this.starRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .filter(star -> canView(user, star.getCollectionItem().getCollection()))
                .map(collectionItemMapper::toResponse)
                .toList();
    }

    private CollectionItem getAccessibleItem(User user, Integer itemId) {
        CollectionItem item = this.collectionItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection item not found"));
        Collection collection = item.getCollection();

        UserCollection membership = this.userCollectionRepository
                .findByUserIdAndCollectionId(user.getId(), collection.getId())
                .orElse(null);

        if (!collection.canView(user, membership)) {
            throw new InvalidDataException("You do not have permission to star this item");
        }

        return item;
    }

    private boolean canView(User user, Collection collection) {
        UserCollection membership = this.userCollectionRepository
                .findByUserIdAndCollectionId(user.getId(), collection.getId())
                .orElse(null);

        return collection.canView(user, membership);
    }
}
