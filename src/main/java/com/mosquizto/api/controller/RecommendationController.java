package com.mosquizto.api.controller;

import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendation")
@Tag(name = "Recommendation system", description = "recommendation base on user need")
public class RecommendationController {
    private final RecommendationService recommendationService ;

    @Operation(summary = "Recommend collection", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("collections")
    public ResponseData<PageResponse<CollectionResponse>> getRecommendedCollectionBaseOnRecent(
            @RequestParam(defaultValue ="0" ,name = "page") int page ,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) throws ExecutionException, InterruptedException {
        return new ResponseData<>(HttpStatus.OK.value(),"Recommended collection" , recommendationService.recommendBaseOnRecent(page,size) ) ;
    }

    @Operation(summary = "Sync all existing collections to Qdrant DB", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/sync/collections")
    public ResponseData<String> syncAllToQdrant() throws ExecutionException, InterruptedException {
        recommendationService.syncAllCollectionsToQdrant();
        return new ResponseData<>(
                HttpStatus.OK.value(),
                "Successfully synchronized collections to Qdrant. Check server logs for details.",
                null
        );
    }
}
