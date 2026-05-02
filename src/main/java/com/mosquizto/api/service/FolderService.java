package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.CreateFolderRequest;
import com.mosquizto.api.dto.request.UpdateFolderRequest;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface FolderService {
    FolderResponse createFolder(CreateFolderRequest createFolderRequest);

    void deleteFolder(Long folderId);

    List<FolderSummaryResponse> getAllOwnFolder();

    FolderResponse getDetailFolder(Long folderId);

    FolderResponse updateFolder(Long folderId, UpdateFolderRequest updateFolderRequest);

    FolderResponse addCollection(Long folderId, Integer collectionId);

    void deleteCollection(@NotNull @Positive Long folderId, @NotNull @Positive Integer collectionId);
}
