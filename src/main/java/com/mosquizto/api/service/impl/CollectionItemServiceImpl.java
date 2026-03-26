package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.AuthenticatedUserService;
import com.mosquizto.api.service.CollectionItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CollectionItemServiceImpl implements CollectionItemService {
    private final CollectionItemRepository collectionItemRepository;
    private final CollectionRepository collectionRepository ;
    private final AuthenticatedUserService authenticatedUserService ;
    @Override
    public CollectionItemResponse addNewItem(CollectionItemRequest request, HttpServletRequest httpServletRequest) {
        var collection = findCollectionById(request.getCollectionId());
        if (!authenticatedUserService.isAuthorOfCollection(httpServletRequest, collection)) {
            throw new InvalidDataException("You do not have permission to add items to this collection");
        }

        CollectionItem newItem = CollectionItemRequest.mapToCollectionItem(request);
        newItem.setCollection(collection);

        return CollectionItemResponse.createResponseBy(collectionItemRepository.save(newItem));
    }

    @Override
    public List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId,HttpServletRequest httpServletRequest) {
        var collection = findCollectionById(collectionId);
        if(collection.getVisibility().equals(false) &&
                !authenticatedUserService.isAuthorOfCollection(httpServletRequest, collection))
        {
            throw new InvalidDataException("You do not have permission to see this collection");
        }
        var response = new ArrayList<CollectionItemResponse>();
        var items = collectionItemRepository.findByCollectionId(collectionId);
        items.forEach( item ->
        {
            response.add(CollectionItemResponse.createResponseBy(item));
        });
        return response ;
    }

    @Override
    public CollectionItemResponse deleteCollectionItem(Integer id, HttpServletRequest httpServletRequest) {
        CollectionItem targetItem = getItemById(id);
        Collection collection = targetItem.getCollection();
        if (!authenticatedUserService.isAuthorOfCollection(httpServletRequest, collection)) {
            throw new InvalidDataException("You do not have permission to delete this item");
        }

        collectionItemRepository.delete(targetItem);
        return CollectionItemResponse.createResponseBy(targetItem);
    }

    @Override
    public CollectionItemResponse updateCollectionItem(Integer id ,CollectionItemRequest request,HttpServletRequest httpServletRequest) {
        var collection = findCollectionById(request.getCollectionId()) ;
        if (!authenticatedUserService.isAuthorOfCollection(httpServletRequest,collection))
        {
            throw new InvalidDataException("You do not have permission to add items to this collection");
        }
        var targetItem = getItemById(id) ;
//        targetItem.setCollection(collection); Không cho phép đổi collection
        targetItem.setTerm(request.getTerm());
        targetItem.setDefinition(request.getDefinition());
        targetItem.setOrderIndex(request.getOrderIndex());
        targetItem.setImageUrl(request.getImageUrl());
        return CollectionItemResponse.createResponseBy(collectionItemRepository.save(targetItem) );
    }

    // Support method;
    private Collection findCollectionById(Integer collectionId)
    {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
    }
    private CollectionItem getItemById(Integer id) {
        return collectionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
}
