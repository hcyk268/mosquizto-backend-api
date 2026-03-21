package com.mosquizto.api.dto.request;

import com.mosquizto.api.model.CollectionItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class CollectionItemRequest implements Serializable {
    private String term ;
    private String definition ;
    private String imageUrl ;
    @NotEmpty(message = "The order of item in collection")
    private int orderIndex ;
    @NotEmpty(message = "The item must belong to an existing collection.")
    private Integer collectionId ;

    public static CollectionItem mapToCollectionItem(CollectionItemRequest request)
    {
        return CollectionItem.builder().
                term(request.term).definition(request.definition).
                imageUrl(request.imageUrl).orderIndex(request.orderIndex).build();
    }
}
