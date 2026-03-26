package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
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
}
