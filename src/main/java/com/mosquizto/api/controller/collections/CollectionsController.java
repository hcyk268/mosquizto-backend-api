package com.mosquizto.api.controller.collections;

import com.meilisearch.sdk.model.SearchResultPaginated;
import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.CollectionService;
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
@Tag(name = "Collection", description = "Flashcard collection APIs")
public class CollectionsController {

    private final CollectionService collectionService;
    private final CollectionSearchService collectionSearchService ;
    @Operation(summary = "Create new collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collections")
    public ResponseData<Integer> create(@Valid @RequestBody CollectionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Create success", collectionService.addCollection(request));
    }

    @Operation(summary = "Get my collections", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me/collections")
    public ResponseData<PageResponse<CollectionResponse>> getMyList(
            @RequestParam(defaultValue = "1" , name = "page") int page,
            @RequestParam(defaultValue = "10" , name = "size") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", collectionService.getMyCollections(page, size));
    }

    @Operation(summary = "Get collection detail")
    @GetMapping("/collections/{id}")
    public ResponseData<CollectionResponse> getDetail(@PathVariable Integer id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", collectionService.getDetail(id));
    }

    @Operation(summary = "Update collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/collections/{id}")
    public ResponseData<Void> update(@PathVariable Integer id, @Valid @RequestBody CollectionRequest request) {
        collectionService.updateCollection(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Update success");
    }

    @Operation(summary = "Delete collection", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/collections/{id}")
    public ResponseData<Void> delete(@PathVariable Integer id) {
        collectionService.deleteCollection(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Delete success");
    }
    @Operation(summary = "Get public collections", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections")
    public ResponseData<PageResponse<CollectionResponse>> getAllPublicCollection(
            @RequestParam(required = false, defaultValue = "public") String visibility,
            @RequestParam(defaultValue = "1" , name = "page") int page,
            @RequestParam(defaultValue = "10" , name = "size") int size)
    {
        if (!"public".equalsIgnoreCase(visibility)) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Unsupported visibility filter");
        }
        PageResponse<CollectionResponse> response = collectionService.getAllPublicCollection(page,size);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", response);
    }
    @Operation(summary = "Search collections", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections/search")
    public ResponseData<SearchResultPaginated > search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String author
    ) {
        SearchResultPaginated response = collectionSearchService.search(q, page, size, author);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", response);
    }
    @GetMapping("/users/me/recent-collections")
    @Operation(summary = "Get recent collections", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<List<CollectionResponse>> getRecentOpened()
    {
        return new ResponseData<>(HttpStatus.OK.value(),"sucess",this.collectionService.getRecentOpenedCollection());
    }
}
