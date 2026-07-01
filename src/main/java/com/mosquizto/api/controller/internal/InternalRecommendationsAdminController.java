package com.mosquizto.api.controller.internal;

import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Internal Recommendations", description = "Internal recommendation maintenance APIs")
public class InternalRecommendationsAdminController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Sync all existing collections to Qdrant DB", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/internal/recommendations/collections/sync")
    public ResponseData<String> syncAllToQdrant() throws ExecutionException, InterruptedException {
        this.recommendationService.syncAllCollectionsToQdrant();
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Successfully synchronized collections to Qdrant. Check server logs for details.",
                null
        );
    }
}
