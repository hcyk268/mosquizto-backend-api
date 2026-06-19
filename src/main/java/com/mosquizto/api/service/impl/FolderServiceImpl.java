package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.request.CreateFolderRequest;
import com.mosquizto.api.dto.request.ShareFolderRequest;
import com.mosquizto.api.dto.request.UpdateFolderRequest;
import com.mosquizto.api.dto.response.FolderMemberResponse;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import com.mosquizto.api.exception.AccessDeniedException;
import com.mosquizto.api.exception.ConflictException;
import com.mosquizto.api.exception.ErrorCode;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.FolderMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.Folder;
import com.mosquizto.api.model.FolderCollection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserFolder;
import com.mosquizto.api.repository.FolderCollectionRepository;
import com.mosquizto.api.repository.FolderRepository;
import com.mosquizto.api.repository.UserFolderRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.FolderService;
import com.mosquizto.api.service.UserService;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.FolderRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

    private final CurrentUserProvider currentUserProvider;
    private final FolderRepository folderRepository;
    private final FolderCollectionRepository folderCollectionRepository;
    private final UserFolderRepository userFolderRepository;
    private final FolderMapper folderMapper;
    private final CollectionService collectionService;
    private final UserService userService;

    @Override
    @Transactional
    public FolderResponse createFolder(CreateFolderRequest createFolderRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = Folder.create(
                user,
                createFolderRequest.getName(),
                createFolderRequest.getDescription()
        );

        Folder savedFolder = this.folderRepository.save(folder);
        this.userFolderRepository.save(UserFolder.createOwner(user, savedFolder));

        return this.folderMapper.toFolderResponse(savedFolder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) {
        User user = this.currentUserProvider.getCurrentUser();
        Folder folder = this.folderRepository.findActiveById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        if (!folder.canDelete(user)) {
            throw new AccessDeniedException("You do not have permission to delete this folder");
        }

        folder.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderSummaryResponse> getAllOwnFolder() {
        User user = this.currentUserProvider.getCurrentUser();

        return this.folderRepository.findAccessibleActiveByUserId(user.getId(), AccessStatus.ENABLE)
                .stream()
                .map(this.folderMapper::toFolderSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponse getDetailFolder(Long folderId) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findActiveByIdWithCollections(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));
        UserFolder membership = getMembership(user.getId(), folderId);

        if (!folder.canView(user, membership)) {
            throw new AccessDeniedException("You do not have permission to view this folder");
        }

        return this.folderMapper.toFolderResponse(folder);
    }

    @Override
    @Transactional
    public FolderResponse updateFolder(Long folderId, UpdateFolderRequest updateFolderRequest) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findActiveByIdWithCollections(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));
        UserFolder membership = getMembership(user.getId(), folderId);

        if (!folder.canManage(user, membership)) {
            throw new AccessDeniedException("You do not have permission to update this folder");
        }

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

        Folder folder = this.folderRepository.findActiveByIdWithCollections(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));
        UserFolder membership = getMembership(user.getId(), folderId);

        if (!folder.canManage(user, membership)) {
            throw new AccessDeniedException("You do not have permission to manage this folder");
        }

        Collection collection = this.collectionService.getById(collectionId);

        boolean accessibility = this.collectionService.isAccessible(collectionId);

        if (!accessibility){
            throw new AccessDeniedException("You might not access this collection");
        }

        if (folder.containsCollection(collection)) {
            throw new ConflictException(ErrorCode.COLLECTION_ALREADY_IN_FOLDER, "Collection is already exists in folder");
        }

        int maxOrderIndex = this.folderCollectionRepository.findMaxActiveOrderIndex(folderId);
        FolderCollection folderCollection = this.folderCollectionRepository.findByFolderIdAndCollectionId(folderId, collectionId)
                .map(existing -> {
                    existing.restore();
                    existing.updateOrder(maxOrderIndex + 1);
                    return existing;
                })
                .orElseGet(() -> folder.addCollection(collection, maxOrderIndex + 1));

        this.folderCollectionRepository.save(folderCollection);

        Folder updatedFolder = this.folderRepository.findActiveByIdWithCollections(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        return folderMapper.toFolderResponse(updatedFolder);
    }

    @Override
    @Transactional
    public void deleteCollection(Long folderId, Integer collectionId) {
        User user = this.currentUserProvider.getCurrentUser();

        Folder folder = this.folderRepository.findActiveByIdWithCollections(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));
        UserFolder membership = getMembership(user.getId(), folderId);

        if (!folder.canManage(user, membership)) {
            throw new AccessDeniedException("You do not have permission to manage this folder");
        }

        Collection collection = this.collectionService.getById(collectionId);
        FolderCollection folderCollection = folder.removeCollection(collection);
        if (folderCollection == null) {
            throw new ResourceNotFoundException("Collection does not exist in folder");
        }

        folderCollection.delete(user);
    }

    @Override
    @Transactional
    public FolderMemberResponse shareFolder(Long folderId, ShareFolderRequest request) {
        User owner = this.currentUserProvider.getCurrentUser();
        Folder folder = this.folderRepository.findActiveById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));

        if (!folder.isOwnedBy(owner)) {
            throw new AccessDeniedException("Only folder owner can share this folder");
        }

        if (FolderRole.OWNER.equals(request.getRole())) {
            throw new InvalidDataException("Role OWNER is reserved for the folder creator");
        }

        User sharedUser = this.userService.getByUsername(request.getUsername());
        if (folder.isOwnedBy(sharedUser)) {
            throw new InvalidDataException("You cannot share this folder to yourself");
        }

        UserFolder membership = this.userFolderRepository
                .findActiveByUserIdAndFolderId(sharedUser.getId(), folderId)
                .orElseGet(() -> UserFolder.createShared(sharedUser, folder, request.getRole()));

        membership.changeRole(request.getRole());
        membership.enable();

        UserFolder savedMembership = this.userFolderRepository.save(membership);
        return this.folderMapper.toFolderMemberResponse(savedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderMemberResponse> getFolderMembers(Long folderId) {
        User user = this.currentUserProvider.getCurrentUser();
        Folder folder = this.folderRepository.findActiveById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not exists"));
        UserFolder membership = getMembership(user.getId(), folderId);

        if (!folder.canView(user, membership)) {
            throw new AccessDeniedException("You do not have permission to view this folder");
        }

        LinkedHashMap<Long, FolderMemberResponse> members = new LinkedHashMap<>();
        members.put(folder.getCreatedBy().getId(), FolderMemberResponse.builder()
                .userId(folder.getCreatedBy().getId())
                .username(folder.getCreatedBy().getUsername())
                .fullName(folder.getCreatedBy().getFullName())
                .imgUri(folder.getCreatedBy().getAvatarUrl())
                .role(FolderRole.OWNER)
                .build());

        this.userFolderRepository.findAllActiveMembersByFolderId(folderId)
                .forEach(userFolder -> members.putIfAbsent(
                        userFolder.getUser().getId(), this.folderMapper.toFolderMemberResponse(userFolder)));

        return List.copyOf(members.values());
    }

    private UserFolder getMembership(Long userId, Long folderId) {
        return this.userFolderRepository.findActiveByUserIdAndFolderId(userId, folderId)
                .orElse(null);
    }
}
