package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CollectionItemRequest;
import com.mosquizto.api.dto.response.CollectionItemResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.CollectionItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.invoker.HttpRequestValues;

@RestController
@RequestMapping("/collection/item")
@RequiredArgsConstructor
@Tag(name = "Collection Item", description = "APIs for managing flashcard collection items")
public class CollectionItemController {
    private final CollectionItemService collectionItemService ;

    @Operation(summary = "create item in collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseData<CollectionItemResponse> addNewItem(HttpServletRequest httpServletRequest,
                                                           @RequestBody CollectionItemRequest request){
        return new ResponseData<CollectionItemResponse>(HttpStatus.OK.value(),"Success :) ",
                collectionItemService.addNewItem(request,httpServletRequest));
    }
}
