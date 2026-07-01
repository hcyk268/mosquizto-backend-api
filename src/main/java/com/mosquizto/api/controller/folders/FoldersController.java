package com.mosquizto.api.controller.folders;

import com.mosquizto.api.dto.request.CreateFolderRequest;
import com.mosquizto.api.dto.request.ShareFolderRequest;
import com.mosquizto.api.dto.request.UpdateFolderRequest;
import com.mosquizto.api.dto.response.FolderMemberResponse;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "Folder", description = "Folder and sharing APIs")
public class FoldersController {

    private final FolderService folderService;

    @Operation(summary = "Create folder", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/folders")
    public ResponseData<FolderResponse> createFolder(@Valid @RequestBody CreateFolderRequest createFolderRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Create folder successfully", this.folderService.createFolder(createFolderRequest));
    }

    @Operation(summary = "Delete folder", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/folders/{folderId}")
    public ResponseData<Void> deleteFolder(@PathVariable Long folderId) {
        this.folderService.deleteFolder(folderId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete folder successfully");
    }

    @Operation(summary = "Get my folders", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users/me/folders")
    public ResponseData<List<FolderSummaryResponse>> getAllOwnFolder() {
        return new ResponseData<List<FolderSummaryResponse>>(HttpStatus.OK.value(), "Get all successfully", this.folderService.getAllOwnFolder());
    }

    @Operation(summary = "Get folder detail", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/folders/{folderId}")
    public ResponseData<FolderResponse> getDetailFolder(@PathVariable Long folderId) {
        return new ResponseData<FolderResponse>(HttpStatus.OK.value(), "Successfully", this.folderService.getDetailFolder(folderId));
    }

    @Operation(summary = "Update folder", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/folders/{folderId}")
    public ResponseData<FolderResponse> updateFolder(@PathVariable Long folderId, @Valid @RequestBody UpdateFolderRequest updateFolderRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Update folder successfully", this.folderService.updateFolder(folderId, updateFolderRequest));
    }

    @Operation(summary = "Add collection to folder", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/folders/{folderId}/collections/{collectionId}")
    public ResponseData<FolderResponse> addCollection(@PathVariable @NotNull @Positive Long folderId, @PathVariable @NotNull @Positive Integer collectionId) {
        return new ResponseData<FolderResponse>(HttpStatus.OK.value(), "Add collection successfully", this.folderService.addCollection(folderId, collectionId));
    }

    @Operation(summary = "Remove collection from folder", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/folders/{folderId}/collections/{collectionId}")
    public ResponseData<Void> deleteCollection(@PathVariable @NotNull @Positive Long folderId, @PathVariable @NotNull @Positive Integer collectionId) {
        this.folderService.deleteCollection(folderId, collectionId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete Success");
    }

    @Operation(summary = "Share folder", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/folders/{folderId}/shares")
    public ResponseData<FolderMemberResponse> shareFolder(@PathVariable @NotNull @Positive Long folderId,
                                                          @Valid @RequestBody ShareFolderRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Share folder successfully",
                this.folderService.shareFolder(folderId, request));
    }

    @Operation(summary = "Get folder members", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/folders/{folderId}/members")
    public ResponseData<List<FolderMemberResponse>> getFolderMembers(@PathVariable @NotNull @Positive Long folderId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get folder members successfully",
                this.folderService.getFolderMembers(folderId));
    }
}
