package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.util.AccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
                                           @Valid @RequestBody ShareCollectionRequest shareCollectionRequest) {
        this.userCollectionService.shareCollection(collectionId, shareCollectionRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Collection shared successfully");
    }

    @GetMapping("/members/{collectionId}")
    public ResponseData<List<MemberResponse>> getAllMembersCollection(@PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", this.userCollectionService.getAllMembersCollection(collectionId));
    }

    @PostMapping("/join/{collectionId}")
    public ResponseData<?> joinCollection(@PathVariable Integer collectionId) {
        this.userCollectionService.joinCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "Joined Successfully");
    }

    @DeleteMapping("/delete/member/{collectionId}/{userId}")
    public ResponseData<?> deleteCollectionMember(@PathVariable Integer collectionId, @PathVariable Long userId) {
        this.userCollectionService.deleteCollectionMember(collectionId, userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Deleted");
    }

    @Operation(summary = "Approve join request", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/approve-join-request")
    public ResponseData<AccessStatus> approveJoinRequest
            (
                    @RequestParam( name = "access_status" ) AccessStatus accessStatus ,
                    @RequestParam( name = "user_id") Long userId ,
                    @RequestParam (name = "collection_id") Integer collectionId
            )
    {
        userCollectionService.approveJoinRequest
                (
                        collectionId, userId, accessStatus
                );
        return new ResponseData<>(HttpStatus.OK.value(),"success",accessStatus);
    }
}
