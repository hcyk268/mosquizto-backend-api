package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.StudySessionDetailsResponse;
import com.mosquizto.api.mapper.StudySessionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.StudySessionDetail;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionItemRepository;
import com.mosquizto.api.repository.StudySessionDetailRepository;
import com.mosquizto.api.repository.StudySessionRepository;
import com.mosquizto.api.service.CollectionService;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.IdempotencyService;
import com.mosquizto.api.service.StudySessionStatsCalculator;
import com.mosquizto.api.util.matching.TextMatcherResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudySessionServiceImplTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CollectionService collectionService;

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private StudySessionDetailRepository studySessionDetailRepository;

    @Mock
    private CollectionItemRepository collectionItemRepository;

    @Mock
    private StudySessionStatsCalculator studySessionStatsCalculator;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TextMatcherResolver textMatcherResolver;

    @Test
    void shouldKeepShowingStudyHistoryWhenCollectionItemIsSoftDeleted() {
        StudySessionServiceImpl service = new StudySessionServiceImpl(
                this.currentUserProvider,
                this.collectionService,
                this.studySessionRepository,
                this.studySessionDetailRepository,
                this.collectionItemRepository,
                new StudySessionMapper(),
                this.studySessionStatsCalculator,
                this.idempotencyService,
                this.transactionManager,
                this.textMatcherResolver
        );

        User user = new User();
        user.setId(1L);
        user.setUsername("alice");

        Collection collection = Collection.initialize(user, "English", "Basics", true);
        collection.setId(10);

        StudySession session = StudySession.start(user, collection, new Date());
        session.setId(20L);

        CollectionItem deletedItem = CollectionItem.builder()
                .term("hello")
                .definition("xin chao")
                .collection(collection)
                .build();
        deletedItem.setId(30);
        deletedItem.setDeletedAt(new Date());

        StudySessionDetail detail = StudySessionDetail.create(session, deletedItem, true, 1200.0, Boolean.TRUE);
        detail.setId(40L);

        when(this.currentUserProvider.getCurrentUsername()).thenReturn("alice");
        when(this.studySessionRepository.findActiveById(20L)).thenReturn(Optional.of(session));
        when(this.studySessionDetailRepository.findAllActiveByStudySessionId(20L))
                .thenReturn(List.of(detail));

        StudySessionDetailsResponse response = service.getSessionDetails(20L);

        assertNotNull(response);
        assertEquals(1, response.getDetails().size());
        assertEquals(30, response.getDetails().get(0).getCollectionItemId());
        assertEquals("hello", response.getDetails().get(0).getQuestion());
        assertEquals("xin chao", response.getDetails().get(0).getCorrectAnswer());
        verify(this.studySessionDetailRepository).findAllActiveByStudySessionId(20L);
    }
}
