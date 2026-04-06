package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
public class CollectionItemResponse implements Serializable {
    private String term ;
    private String definition ;
    private String imageUrl ;
    private Integer orderIndex ;
    private Integer collectionId ;
    private LocalDateTime createAt ;
    private LocalDateTime updateAt ;
}
