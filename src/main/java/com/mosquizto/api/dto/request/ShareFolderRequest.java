package com.mosquizto.api.dto.request;

import com.mosquizto.api.util.FolderRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ShareFolderRequest implements Serializable {

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotNull(message = "role must be not null")
    private FolderRole role;
}
