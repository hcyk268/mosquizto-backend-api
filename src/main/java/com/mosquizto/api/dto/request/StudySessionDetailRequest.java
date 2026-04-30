package com.mosquizto.api.dto.request;

import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.StudySessionDetail;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class StudySessionDetailRequest {
    private Long sessionId  ;

    private Integer itemId ;

    private Boolean isCorrect;

    private Double responseTimeMs;
}
