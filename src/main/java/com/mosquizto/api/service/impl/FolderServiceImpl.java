package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CreateFolderRequest;
import com.mosquizto.api.dto.request.UpdateFolderRequest;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.FolderMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.Folder;
import com.mosquizto.api.model.FolderCollection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.FolderCollectionRepository;
import com.mosquizto.api.repository.FolderRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

    private final CurrentUserProvider currentUserProvider;
    private final FolderRepository folderRepository;
    private final FolderCollectionRepository folderCollectionRepository;
    private final FolderMapper folderMapper;
    private final CollectionService collectionService;

    @Override
    public FolderResponse createFolder(CreateFolderRequest createFolderRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = Folder.create(
                user,
                createFolderRequest.getName(),
                createFolderRequest.getDescription()
        );

        this.folderRepository.save(folder);

        return this.folderMapper.toFolderResponse(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) {

        Folder folder = this.folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        User user = this.currentUserProvider.getCurrentUser();

        if (!folder.canManage(user)) {
            throw new InvalidDataException("You do not have permission to delete this folder");
        }

        this.folderCollectionRepository.deleteAllByFolderId(folderId);
        this.folderRepository.delete(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderSummaryResponse> getAllOwnFolder() {
        User user = this.currentUserProvider.getCurrentUser();

        return this.folderRepository.findAllByCreatedByIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this.folderMapper::toFolderSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponse getDetailFolder(Long folderId) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findByIdAndCreatedByIdWithCollections(folderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        if (!folder.canView(user)) {
            throw new InvalidDataException("You do not have permission to view this folder");
        }

        return this.folderMapper.toFolderResponse(folder);
    }

    @Override
    @Transactional
    public FolderResponse updateFolder(Long folderId, UpdateFolderRequest updateFolderRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findByIdAndCreatedByIdWithCollections(folderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        boolean hasUpdatedField = false;

        if (updateFolderRequest.getName() != null) {
            if (!StringUtils.hasText(updateFolderRequest.getName())) {
                throw new InvalidDataException("name must be not blank");
            }
            hasUpdatedField = true;
        }

        if (updateFolderRequest.getDescription() != null) {
            if (!StringUtils.hasText(updateFolderRequest.getDescription())) {
                throw new InvalidDataException("description must be not blank");
            }
            hasUpdatedField = true;
        }

        if (!hasUpdatedField) {
            throw new InvalidDataException("At least one field must be provided");
        }

        folder.updateInfo(
                updateFolderRequest.getName(),
                updateFolderRequest.getDescription()
        );

        return this.folderMapper.toFolderResponse(folder);
    }

    @Override
    @Transactional
    public FolderResponse addCollection(Long folderId, Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findByIdAndCreatedById(folderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        Collection collection = this.collectionService.getById(collectionId);

        boolean accessibility = this.collectionService.isAccessible(collectionId);

        if (!accessibility){
            throw new InvalidDataException("You might not access this collection");
        }

        if (folder.containsCollection(collection)) {
            throw new InvalidDataException("Collection is already exists in folder");
        }

        int maxOrderIndex = this.folderCollectionRepository.findMaxOrderIndexCollection(folderId);

        FolderCollection folderCollection = folder.addCollection(collection, maxOrderIndex + 1);

        this.folderCollectionRepository.save(folderCollection);

        Folder updatedFolder = this.folderRepository.findByIdAndCreatedByIdWithCollections(folderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        return folderMapper.toFolderResponse(updatedFolder);
    }

    @Override
    @Transactional
    public void deleteCollection(Long folderId, Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findByIdAndCreatedById(folderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        FolderCollection folderCollection = this.folderCollectionRepository.findByFolderIdAndCollectionId(folderId, collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection does not exist in folder"));

        folder.removeCollection(folderCollection.getCollection());

        this.folderCollectionRepository.delete(folderCollection);
    }
}
