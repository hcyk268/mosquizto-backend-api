package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.StarredCollectionItemResponse;
import com.mosquizto.api.service.CollectionItemStarService;
import com.mosquizto.api.service.CollectionItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collection/item")
@RequiredArgsConstructor
@Tag(name = "Collection Item", description = "Collection item and star APIs")
public class CollectionItemController {
    private final CollectionItemService collectionItemService;
    private final CollectionItemStarService collectionItemStarService;

    @Operation(summary = "Create item", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseData<CollectionItemResponse> addNewItem(@Valid @RequestBody CollectionItemRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(),"Success :) ",
                collectionItemService.addNewItem(request));
    }

    @Operation(summary = "Get collection items" , security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseData<List<CollectionItemResponse>> getItemsByCollectionId(@PathVariable("id") Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.getItemsByCollectionId(collectionId));
    }

    @Operation(summary = "Delete item", security = @SecurityRequirement(name= "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseData<CollectionItemResponse> deleteItem(@PathVariable Integer id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.deleteCollectionItem(id)
        );
    }

    @Operation(summary = "Update item" , security =  @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseData<CollectionItemResponse> updateItem(@PathVariable Integer id,
                                                           @Valid @RequestBody CollectionItemRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.updateCollectionItem(id, request));
    }

    @Operation(summary = "Star item", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}/star")
    public ResponseData<StarredCollectionItemResponse> starItem(@PathVariable Integer id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Star item successfully",
                collectionItemStarService.starItem(id));
    }

    @Operation(summary = "Unstar item", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}/star")
    public ResponseData<Void> unstarItem(@PathVariable Integer id) {
        collectionItemStarService.unstarItem(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Unstar item successfully");
    }

    @Operation(summary = "Get my starred items", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/starred")
    public ResponseData<List<StarredCollectionItemResponse>> getMyStarredItems() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get starred items successfully",
                collectionItemStarService.getMyStarredItems());
    }
}
