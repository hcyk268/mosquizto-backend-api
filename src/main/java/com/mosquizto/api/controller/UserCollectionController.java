package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user-collection")
public class UserCollectionController {

    private final UserCollectionService userCollectionService;

    @PostMapping("/share/{collectionId}")
    public ResponseData<?> shareCollection(@PathVariable Integer collectionId,
                                           @Valid @RequestBody ShareCollectionRequest shareCollectionRequest,
                                           HttpServletRequest request) {

        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        this.userCollectionService.shareCollection(token, collectionId, shareCollectionRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Collection shared successfully");
    }

    @GetMapping("/members/{collectionId}")
    public ResponseData<List<MemberResponse>> getAllMembersCollection(@PathVariable Integer collectionId, HttpServletRequest request) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", this.userCollectionService.getAllMembersCollection(token, collectionId));
    }

    @PostMapping("/join/{collectionId}")
    public ResponseData<?> joinCollection(@PathVariable Integer collectionId, HttpServletRequest request) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        this.userCollectionService.joinCollection(token, collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "Joined Successfully");
    }

    @DeleteMapping("/delete/member/{collectionId}/{userId}")
    public ResponseData<?> deleteCollectionMember(HttpServletRequest request,
                                                  @PathVariable Integer collectionId, @PathVariable Long userId) {
        String token = AuthorizationHeaderUtils.extractRequiredBearerToken(request);
        this.userCollectionService.deleteCollectionMember(token, collectionId, userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Deleted");
    }
}
