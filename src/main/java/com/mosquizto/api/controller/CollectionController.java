package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collection")
@RequiredArgsConstructor
@Tag(name = "Collection", description = "APIs for managing flashcard collections")
public class CollectionController {

    private final CollectionService collectionService;

    @Operation(summary = "Create new collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseData<Integer> create(@Valid @RequestBody CollectionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Create success", collectionService.addCollection(request));
    }

    @Operation(summary = "Get my collections", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-list")
    public ResponseData<PageResponse<CollectionResponse>> getMyList(
            @RequestParam(defaultValue = "1" , name = "page") int page,
            @RequestParam(defaultValue = "10" , name = "size") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", collectionService.getMyCollections(page, size));
    }

    @Operation(summary = "Get collection detail")
    @GetMapping("/{id}")
    public ResponseData<CollectionResponse> getDetail(@PathVariable Integer id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", collectionService.getDetail(id));
    }

    @Operation(summary = "Update collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseData<Void> update(@PathVariable Integer id, @Valid @RequestBody CollectionRequest request) {
        collectionService.updateCollection(id, request);
        return new ResponseData<>(HttpStatus.OK.value(), "Update success");
    }

    @Operation(summary = "Delete collection", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable Integer id) {
        collectionService.deleteCollection(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Delete success");
    }
}
