package com.mosquizto.api.controller.collections;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Collection Member", description = "Collection sharing and member APIs")
public class CollectionMembershipController {

    private final UserCollectionService userCollectionService;

    @Operation(summary = "Share collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collections/{collectionId}/shares")
    public ResponseData<Void> shareCollection(@PathVariable Integer collectionId,
                                              @Valid @RequestBody ShareCollectionRequest shareCollectionRequest) {
        this.userCollectionService.shareCollection(collectionId, shareCollectionRequest);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Collection shared successfully");
    }

    @Operation(summary = "Get collection members", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections/{collectionId}/members")
    public ResponseData<List<MemberResponse>> getAllMembersCollection(@PathVariable Integer collectionId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success",
                this.userCollectionService.getAllMembersCollection(collectionId));
    }

    @Operation(summary = "Join collection", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/collections/{collectionId}/join-requests")
    public ResponseData<Void> joinCollection(@PathVariable Integer collectionId) {
        this.userCollectionService.joinCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "Joined Successfully");
    }

    @Operation(summary = "Remove collection member", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/collections/{collectionId}/members/{userId}")
    public ResponseData<Void> deleteCollectionMember(@PathVariable Integer collectionId, @PathVariable Long userId) {
        this.userCollectionService.deleteCollectionMember(collectionId, userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Deleted");
    }

    @Operation(summary = "Approve join request", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/collections/{collectionId}/join-requests/{userId}")
    public ResponseData<AccessStatus> approveJoinRequest(@RequestParam(name = "status") AccessStatus accessStatus,
                                                         @PathVariable Long userId,
                                                         @PathVariable Integer collectionId) {
        this.userCollectionService.approveJoinRequest(collectionId, userId, accessStatus);
        return new ResponseData<>(HttpStatus.OK.value(), "success", accessStatus);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/users/me/recent-collections/{collectionId}")
    public ResponseData<Void> removeFromRecent(@PathVariable Integer collectionId) {
        this.userCollectionService.removeRecentOpenedCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "success");
    }

    @Operation(summary = "Get all pending invitations", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me/collection-invitations")
    public ResponseData<List<ShareCollectionResponse>> getInvitations() {
        return new ResponseData<>(HttpStatus.OK.value(), "success",
                this.userCollectionService.getMyPendingInvitations());
    }

    @Operation(summary = "respond to invitation", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/collections/{collectionId}/invitations/me")
    public ResponseData<Void> respondInvitation(@PathVariable Integer collectionId,
                                                @RequestParam("status") AccessStatus accessStatus) {
        this.userCollectionService.respondToShareInvite(collectionId, accessStatus);
        return new ResponseData<>(HttpStatus.OK.value(), "success");
    }

    @Operation(summary = "Get role of user in collection", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/collections/{collectionId}/role/me")
    public ResponseData<CollectionRole> getRoleOfCollection(@PathVariable Integer collectionId) {
        CollectionRole role = this.userCollectionService.getRoleOfCollection(collectionId);
        return new ResponseData<>(HttpStatus.OK.value(), "success", role);
    }
}
