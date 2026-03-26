package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionItemService;
import com.mosquizto.api.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.invoker.HttpRequestValues;

import java.util.List;

@RestController
@RequestMapping("/collection/item")
@RequiredArgsConstructor
@Tag(name = "Collection Item", description = "APIs for managing flashcard collection items")
public class CollectionItemController {
    private final CollectionItemService collectionItemService ;

    @Operation(summary = "create item in collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseData<CollectionItemResponse> addNewItem(HttpServletRequest httpServletRequest,
                                                           @Valid @RequestBody CollectionItemRequest request){
        return new ResponseData<>(HttpStatus.OK.value(),"Success :) ",
                collectionItemService.addNewItem(request,httpServletRequest));
    }
    @Operation(summary = "get all items in a collection" , security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("{id}")
    public ResponseData<List<CollectionItemResponse>> getItemsByCollectionId(HttpServletRequest httpServletRequest,
                                                                   @PathVariable("id") Integer collectionId)
    {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.getItemsByCollectionId(collectionId,httpServletRequest));
    }

    @Operation(summary = "delete item form collection", security = @SecurityRequirement(name= "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseData<CollectionItemResponse> deleteItem(@PathVariable  Integer id,
                                                           HttpServletRequest httpServletRequest){
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.deleteCollectionItem(id,httpServletRequest)
        );
    }
    @Operation(summary = "update item" , security =  @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("{id}")
    public ResponseData<CollectionItemResponse> updateItem(@PathVariable Integer id ,
                                                           @Valid @RequestBody CollectionItemRequest request,
                                                           HttpServletRequest httpServletRequest)
    {
        return new ResponseData<>(HttpStatus.OK.value() , "Success",
                collectionItemService.updateCollectionItem(id, request,httpServletRequest));
    }
}
