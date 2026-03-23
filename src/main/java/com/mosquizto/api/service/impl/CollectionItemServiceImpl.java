package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.AuthenticatedUserService;
import com.mosquizto.api.service.CollectionItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
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
        User user = authenticatedUserService.getAuthenticatedUser(httpServletRequest);
        Collection collection = findCollectionById(request.getCollectionId());

        if (!user.getId().equals(collection.getUser().getId())) {
            throw new InvalidDataException("You do not have permission to add items to this collection");
        }

        CollectionItem newItem = CollectionItemRequest.mapToCollectionItem(request);
        newItem.setCollection(collection);

        return CollectionItemResponse.createResponseBy(collectionItemRepository.save(newItem));
    }

    @Override
    public List<CollectionItemResponse> getItemsByCollectionId(Integer collectionId,HttpServletRequest httpServletRequest) {
        var collection = findCollectionById(collectionId);
        var user = authenticatedUserService.getAuthenticatedUser(httpServletRequest) ;
        if(collection.getVisibility().equals(false) &&
            !user.getId().equals(collection.getUser().getId()) ) // Creator doesn't share this collection
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

    // Support method;
    private Collection findCollectionById(Integer collectionId)
    {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
    }
}
