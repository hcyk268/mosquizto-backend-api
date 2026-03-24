package com.mosquizto.api.dto.request;

import com.mosquizto.api.util.CollectionRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ShareCollectionRequest implements Serializable {

    @NotEmpty(message = "Username must be not empty")
    private String username;

    @NotNull(message = "Role of collection must be not null")
    private CollectionRole role;
}
