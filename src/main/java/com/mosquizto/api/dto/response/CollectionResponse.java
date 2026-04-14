package com.mosquizto.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class CollectionResponse implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private Boolean visibility;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
    private Integer count ;
}