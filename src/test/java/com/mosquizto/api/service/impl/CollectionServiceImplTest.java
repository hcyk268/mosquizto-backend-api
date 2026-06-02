package com.mosquizto.api.service.impl;

import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.UserCollectionRepository;
import com.mosquizto.api.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionServiceImplTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private UserCollectionRepository userCollectionRepository;

    @Mock
    private UserCollectionService userCollectionService;

    @Mock
    private CollectionSearchService collectionSearchService;

    @Mock
    private CollectionMembershipResolver membershipResolver;

    private CollectionServiceImpl collectionService;

    @Mock
    private EmbeddingService embeddingService ;
    @Mock
    private VectorStoreService vectorStoreService ;
    @BeforeEach
    void setUp() {
        this.collectionService = new CollectionServiceImpl(
                this.collectionRepository,
                this.currentUserProvider,
                this.collectionMapper,
                this.userCollectionRepository,
                this.userCollectionService,
                this.collectionSearchService,
                this.membershipResolver,
                this.embeddingService,
                this.vectorStoreService

        );
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    void shouldSoftDeleteCollectionAndDeleteSearchDocumentAfterCommit() {
        User user = new User();
        user.setId(1L);
        Collection collection = Collection.initialize(user, "Java", "Backend", true);
        collection.setId(7);
        when(this.currentUserProvider.getCurrentUser()).thenReturn(user);
        when(this.collectionRepository.findActiveById(7)).thenReturn(Optional.of(collection));
        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.initSynchronization();

        this.collectionService.deleteCollection(7);

        verify(this.membershipResolver).requireCanDelete(collection, user);
        assertNotNull(collection.getDeletedAt());
        assertSame(user, collection.getDeletedBy());
        verify(this.collectionSearchService, never()).delete(7);

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(TransactionSynchronization::afterCommit);

        verify(this.collectionSearchService).delete(7);
    }
}
