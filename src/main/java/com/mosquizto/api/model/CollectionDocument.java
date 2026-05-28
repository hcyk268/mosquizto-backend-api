package com.mosquizto.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDocument {
    private Integer id;
    private String title;
    private String description;
    private String titleNgrams;
    private String descriptionNgrams;
    private Boolean visibility;
    private String createdByUsername;
    private Integer count;
}
