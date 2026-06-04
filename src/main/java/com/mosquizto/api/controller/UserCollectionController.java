package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.dto.response.ShareCollectionResponse;
import com.mosquizto.api.service.UserCollectionService;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Collection Member", description = "Collection sharing and member APIs")
public class UserCollectionController {

    private final UserCollectionService userCollectionService;

    @Operation(summary = "Share collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/share/{collectionId}")
    public ResponseData<Void> shareCollection(@PathVariable Integer collectionId,
                                           @Valid @RequestBody ShareCollectionRequest shareCollectionRequest) {
        this.userCollectionService.shareCollection(collectionId, shareCollectionRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Collection shared successfully");
    }

    @Operation(summary = "Get collection members", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/members/{collectionId}")
    public ResponseData<List<MemberResponse>> getAllMembersCollection(@PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", this.userCollectionService.getAllMembersCollection(collectionId));
    }

    @Operation(summary = "Join collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/join/{collectionId}")
    public ResponseData<?> joinCollection(@PathVariable Integer collectionId) {
        this.userCollectionService.joinCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "Joined Successfully");
    }

    @Operation(summary = "Remove collection member", security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/collection/{collectionId}/recent")
    public ResponseData<Void> removeFromRecent(
            @PathVariable Integer collectionId
    ) {
        userCollectionService.removeRecentOpenedCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(),"success");
    }

    @Operation(summary = "Get all pending invitations" ,  security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/invitations")
    public ResponseData<List<ShareCollectionResponse>> getInvitations()
    {
        return  new ResponseData<>(HttpStatus.OK.value(),"success", userCollectionService.getMyPendingInvitations()) ;
    }

    @Operation(summary = "respond to invitation" , security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/respond/invitation")
    public ResponseData<Void> respondInvitation(
            @RequestParam("collectionId" )Integer collectionId ,
            @RequestParam("accessStatus") AccessStatus accessStatus
    )
    {
        userCollectionService.respondToShareInvite(collectionId,accessStatus) ;
        return new ResponseData<>(HttpStatus.OK.value(), "success") ;
    }
    @Operation(summary = "Get role of user in collection", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collection-role")
    public ResponseData<CollectionRole> getRoleOfCollection(
            @RequestParam("collectionId") Integer collectionId
    ) {
        // Không cần inviterId nữa
        CollectionRole role = userCollectionService.getRoleOfCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "success", role);
    }
}

