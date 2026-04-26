package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_study_session_detail")
public class StudySessionDetail extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private StudySession studySession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_item_id", nullable = false)
    private CollectionItem collectionItem;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "response_time_ms")
    private Double responseTimeMs;

    public static StudySessionDetail create(StudySession studySession,
                                            CollectionItem collectionItem,
                                            boolean isCorrect,
                                            Double responseTimeMs) {
        return StudySessionDetail.builder()
                .studySession(studySession)
                .collectionItem(collectionItem)
                .isCorrect(isCorrect)
                .responseTimeMs(responseTimeMs)
                .build();
    }
}
