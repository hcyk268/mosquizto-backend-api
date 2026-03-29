package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_study_session")
public class StudySession extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "total_correct")
    private Integer totalCorrect;

    @Column(name = "total_wrong")
    private Integer totalWrong;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "completed_at")
    private Date completedAt;

    @OneToMany(mappedBy = "studySession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudySessionDetail> studySessionDetails = new ArrayList<>();

    public static StudySession start(User user, Collection collection, Date startedAt) {
        return StudySession.builder()
                .user(user)
                .collection(collection)
                .totalScore(0)
                .totalWrong(0)
                .totalCorrect(0)
                .startedAt(startedAt)
                .build();
    }

    public boolean isOwnedBy(String username) {
        return this.user != null
                && this.user.getUsername() != null
                && this.user.getUsername().equals(username);
    }

    public boolean isCompleted() {
        return this.completedAt != null;
    }

    public StudySessionDetail recordAnswer(CollectionItem collectionItem, boolean isCorrect, Integer responseTimeMs) {
        ensureActive();

        StudySessionDetail detail = StudySessionDetail.create(this, collectionItem, isCorrect, responseTimeMs);

        if (this.studySessionDetails == null) {
            this.studySessionDetails = new ArrayList<>();
        }
        this.studySessionDetails.add(detail);

        if (isCorrect) {
            this.totalCorrect = safeValue(this.totalCorrect) + 1;
            this.totalScore = safeValue(this.totalScore) + 1;
        } else {
            this.totalWrong = safeValue(this.totalWrong) + 1;
        }

        return detail;
    }

    public void complete(Date completedAt) {
        ensureActive();
        this.completedAt = completedAt;
    }

    public double calculateAccuracyRate() {
        int totalCorrect = safeValue(this.totalCorrect);
        int totalWrong = safeValue(this.totalWrong);
        int totalAnswered = totalCorrect + totalWrong;

        if (totalAnswered == 0) {
            return 0.0;
        }

        return (double) totalCorrect / totalAnswered * 100;
    }

    public long calculateDurationMs() {
        if (this.startedAt == null || this.completedAt == null) {
            return 0L;
        }

        return this.completedAt.getTime() - this.startedAt.getTime();
    }

    private void ensureActive() {
        if (isCompleted()) {
            throw new InvalidDataException("This study session has already been completed");
        }
    }

    private int safeValue(Integer value) {
        return value == null ? 0 : value;
    }
}
