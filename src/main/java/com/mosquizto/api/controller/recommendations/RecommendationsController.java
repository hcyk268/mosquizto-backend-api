package com.mosquizto.api.controller.recommendations;

import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;


@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Recommendation system", description = "recommendation base on user need")
public class RecommendationsController {
    private final RecommendationService recommendationService ;

    @Operation(summary = "Recommend collection", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/recommendations/collections")
    public ResponseData<PageResponse<CollectionResponse>> getRecommendedCollectionBaseOnRecent(
            @RequestParam(defaultValue ="0" ,name = "page") int page ,
            @RequestParam(defaultValue = "10", name = "size") int size
    ) throws ExecutionException, InterruptedException {
        return new ResponseData<>(HttpStatus.OK.value(),"Recommended collection" , recommendationService.recommendBaseOnRecent(page,size) ) ;
    }

}
