package com.mosquizto.api.controller.internal;

import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Internal Collections", description = "Internal collection maintenance APIs")
public class InternalCollectionsAdminController {

    private final CollectionSearchService collectionSearchService;

    @Operation(summary = "Reindex collections", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/internal/collections/search-index/rebuild")
    public ResponseData<Void> createIndex() {
        this.collectionSearchService.reindexAll();
        return new ResponseData<>(HttpStatus.OK.value(), "success");
    }
}
