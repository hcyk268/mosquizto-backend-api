package com.mosquizto.api.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateFolderRequest implements Serializable {

    @NotBlank(message = "name must be not blank")
    @Size(max = 255, message = "name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "description must be not blank")
    private String description;

}
