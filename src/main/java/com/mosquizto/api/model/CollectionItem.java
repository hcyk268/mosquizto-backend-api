package com.mosquizto.api.model;

import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.util.matching.TextMatcher;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_collection_item")
public class CollectionItem extends AbstractEntity<Integer> {
    @Column(name = "term")
    private String term;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne(targetEntity = Collection.class , fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection ;

    @OneToMany(mappedBy = "collectionItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudySessionDetail> studySessionDetails = new ArrayList<>();

    public boolean belongsTo(Collection collection) {
        return this.collection != null
                && this.collection.getId() != null
                && collection != null
                && collection.getId() != null
                && this.collection.getId().equals(collection.getId());
    }

    public String correctAnswerFor(Boolean mode) {
        return Boolean.TRUE.equals(mode) ? this.definition : this.term;
    }

    public boolean matchesAnswer(Boolean mode, String submittedTerm, String submittedDefinition, TextMatcher textMatcher, double threshold) {
        String expectedAnswer = correctAnswerFor(mode);
        String submittedAnswer = Boolean.TRUE.equals(mode) ? submittedDefinition : submittedTerm;
        return textMatcher.match(expectedAnswer, submittedAnswer) >= threshold;
    }

    public void updateContent(String term, String definition, String imageUrl, Integer orderIndex) {
        if (term != null) {
            this.term = term;
        }

        if (definition != null) {
            this.definition = definition;
        }

        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }

        if (orderIndex != null) {
            this.orderIndex = orderIndex;
        }
    }

    public void assignTo(Collection collection) {
        if (collection == null) {
            throw new InvalidDataException("Collection must not be null");
        }

        this.collection = collection;
    }

    public void delete(User deleteBy) {
        this.setDeletedAt(new Date());
        this.setDeletedBy(deleteBy);
    }
}


