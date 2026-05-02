package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.CreateFolderRequest;
import com.mosquizto.api.dto.request.UpdateFolderRequest;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.FolderService;
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
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/create")
    public ResponseData<FolderResponse> createFolder(@Valid @RequestBody CreateFolderRequest createFolderRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Create folder successfully", this.folderService.createFolder(createFolderRequest));
    }

    @DeleteMapping("/delete/{folderId}")
    public ResponseData<Void> deleteFolder(@PathVariable Long folderId) {
        this.folderService.deleteFolder(folderId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete folder successfully");
    }

    @GetMapping("/all")
    public ResponseData<List<FolderSummaryResponse>> getAllOwnFolder() {
        return new ResponseData<List<FolderSummaryResponse>>(HttpStatus.OK.value(), "Get all successfully", this.folderService.getAllOwnFolder());
    }

    @GetMapping("/{folderId}")
    public ResponseData<FolderResponse> getDetailFolder(@PathVariable Long folderId) {
        return new ResponseData<FolderResponse>(HttpStatus.OK.value(), "Successfully", this.folderService.getDetailFolder(folderId));
    }

    @PatchMapping("/{folderId}")
    public ResponseData<FolderResponse> updateFolder(@PathVariable Long folderId, @Valid @RequestBody UpdateFolderRequest updateFolderRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Update folder successfully", this.folderService.updateFolder(folderId, updateFolderRequest));
    }

    @PostMapping("/{folderId}/collection/{collectionId}")
    public ResponseData<FolderResponse> addCollection(@PathVariable @NotNull @Positive Long folderId, @PathVariable @NotNull @Positive Integer collectionId) {
        return new ResponseData<FolderResponse>(HttpStatus.OK.value(), "Add collection successfully", this.folderService.addCollection(folderId, collectionId));
    }

    @DeleteMapping("/{folderId}/collection/{collectionId}")
    public ResponseData<Void> deleteCollection(@PathVariable @NotNull @Positive Long folderId, @PathVariable @NotNull @Positive Integer collectionId) {
        this.folderService.deleteCollection(folderId, collectionId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete Success");
    }
}
