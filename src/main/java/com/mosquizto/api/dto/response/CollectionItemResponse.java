package com.mosquizto.api.dto.response;

import com.mosquizto.api.model.CollectionItem;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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

    public static CollectionItemResponse createResponseBy(CollectionItem item)
    {
        return CollectionItemResponse.builder().term(item.getTerm()).
                definition(item.getDefinition()).imageUrl(item.getImageUrl()).orderIndex(item.getOrderIndex())
                .collectionId(item.getCollection().getId()).createAt(LocalDateTime.now()).build();
    }
}
