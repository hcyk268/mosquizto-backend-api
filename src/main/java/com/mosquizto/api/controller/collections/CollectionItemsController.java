package com.mosquizto.api.controller.collections;

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
@RequiredArgsConstructor
@Tag(name = "Collection Item", description = "Collection item and star APIs")
public class CollectionItemsController {
    private final CollectionItemService collectionItemService;
    private final CollectionItemStarService collectionItemStarService;

    @Operation(summary = "Create item", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collections/{collectionId}/items")
    public ResponseData<CollectionItemResponse> addNewItem(@PathVariable Integer collectionId,
                                                           @Valid @RequestBody CollectionItemRequest request) {
        request.setCollectionId(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(),"Success :) ",
                collectionItemService.addNewItem(request));
    }

    @Operation(summary = "Get collection items" , security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections/{collectionId}/items")
    public ResponseData<List<CollectionItemResponse>> getItemsByCollectionId(@PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.getItemsByCollectionId(collectionId));
    }

    @Operation(summary = "Delete item", security = @SecurityRequirement(name= "bearerAuth"))
    @DeleteMapping("/collections/{collectionId}/items/{itemId}")
    public ResponseData<CollectionItemResponse> deleteItem(@PathVariable Integer collectionId,
                                                           @PathVariable Integer itemId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.deleteCollectionItem(itemId)
        );
    }

    @Operation(summary = "Update item" , security =  @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/collections/{collectionId}/items/{itemId}")
    public ResponseData<CollectionItemResponse> updateItem(@PathVariable Integer collectionId,
                                                           @PathVariable Integer itemId,
                                                           @Valid @RequestBody CollectionItemRequest request) {
        request.setCollectionId(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                collectionItemService.updateCollectionItem(itemId, request));
    }

    @Operation(summary = "Star item", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collection-items/{id}/star")
    public ResponseData<StarredCollectionItemResponse> starItem(@PathVariable Integer id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Star item successfully",
                collectionItemStarService.starItem(id));
    }

    @Operation(summary = "Unstar item", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/collection-items/{id}/star")
    public ResponseData<Void> unstarItem(@PathVariable Integer id) {
        collectionItemStarService.unstarItem(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Unstar item successfully");
    }

    @Operation(summary = "Get my starred items", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me/starred-collection-items")
    public ResponseData<List<StarredCollectionItemResponse>> getMyStarredItems() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get starred items successfully",
                collectionItemStarService.getMyStarredItems());
    }
}
