package com.mosquizto.api.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartStudySessionRequest implements Serializable {

    @JsonAlias({"collectionId", "collectionid", "collection_id"})
    @NotNull
    private Integer collectionId;

}
