package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolderRequest implements Serializable {

    @Size(max = 255, message = "name must not exceed 255 characters")
    private String name;

    private String description;
}
