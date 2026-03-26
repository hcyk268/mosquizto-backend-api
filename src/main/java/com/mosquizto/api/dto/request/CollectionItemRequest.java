package com.mosquizto.api.dto.request;

import com.mosquizto.api.model.CollectionItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class CollectionItemRequest implements Serializable {
    @NotBlank(message = "Term cannot be blank")
    private String term;

    @NotBlank(message = "Definition cannot be blank")
    private String definition;

    private String imageUrl;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    @NotNull(message = "Collection ID is required")
    private Integer collectionId;

    public static CollectionItem mapToCollectionItem(CollectionItemRequest request)
    {
        return CollectionItem.builder().
                term(request.term).definition(request.definition).
                imageUrl(request.imageUrl).orderIndex(request.orderIndex).build();
    }
}
