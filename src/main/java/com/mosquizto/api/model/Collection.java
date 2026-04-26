package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_collection")
@Entity
@Builder
@Data
public class Collection extends AbstractEntity<Integer> {

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description ;
    private Boolean visibility ;

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CollectionItem> collectionItems = new ArrayList<>();

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudySession> studySessions = new ArrayList<>();

    @ColumnDefault("0")
    private Integer count;
}


