package com.mosquizto.api.service.impl;

import com.mosquizto.api.mapper.FolderMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.Folder;
import com.mosquizto.api.model.FolderCollection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.FolderCollectionRepository;
import com.mosquizto.api.repository.FolderRepository;
import com.mosquizto.api.repository.UserFolderRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private FolderCollectionRepository folderCollectionRepository;

    @Mock
    private UserFolderRepository userFolderRepository;

    @Mock
    private FolderMapper folderMapper;

    @Mock
    private CollectionService collectionService;

    @Mock
    private UserService userService;

    @Test
    void shouldSoftDeleteFolderWithoutRemovingCollections() {
        FolderServiceImpl service = new FolderServiceImpl(
                this.currentUserProvider,
                this.folderRepository,
                this.folderCollectionRepository,
                this.userFolderRepository,
                this.folderMapper,
                this.collectionService,
                this.userService
        );

        User user = new User();
        user.setId(1L);

        Folder folder = Folder.create(user, "Favorites", "Study plan");
        folder.setId(10L);

        when(this.currentUserProvider.getCurrentUser()).thenReturn(user);
        when(this.folderRepository.findActiveById(10L)).thenReturn(Optional.of(folder));

        service.deleteFolder(10L);

        assertNotNull(folder.getDeletedAt());
        assertSame(user, folder.getDeletedBy());
        verify(this.folderCollectionRepository, never()).deleteAllActiveByFolderId(10L);
        verify(this.folderRepository, never()).delete(folder);
    }

    @Test
    void shouldSoftDeleteFolderCollectionWhenRemovingCollectionFromFolder() {
        FolderServiceImpl service = new FolderServiceImpl(
                this.currentUserProvider,
                this.folderRepository,
                this.folderCollectionRepository,
                this.userFolderRepository,
                this.folderMapper,
                this.collectionService,
                this.userService
        );

        User user = new User();
        user.setId(1L);

        Folder folder = Folder.create(user, "Favorites", "Study plan");
        folder.setId(10L);

        Collection collection = Collection.builder().build();
        collection.setId(20);

        FolderCollection folderCollection = FolderCollection.create(folder, collection, 1);
        folderCollection.setId(30L);
        folder.getFolderCollections().add(folderCollection);

        when(this.currentUserProvider.getCurrentUser()).thenReturn(user);
        when(this.folderRepository.findActiveByIdWithCollections(10L)).thenReturn(Optional.of(folder));
        when(this.collectionService.getById(20)).thenReturn(collection);

        service.deleteCollection(10L, 20);

        assertNotNull(folderCollection.getDeletedAt());
        assertSame(user, folderCollection.getDeletedBy());
        assertTrue(folder.getFolderCollections().isEmpty());
        verify(this.collectionService).getById(20);
        verify(this.folderCollectionRepository, never()).delete(folderCollection);
    }
}
