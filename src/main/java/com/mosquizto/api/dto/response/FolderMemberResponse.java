package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.FolderRole;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class FolderMemberResponse implements Serializable {
    private Long userId;
    private String username;
    private String fullName;
    private String imgUri;
    private FolderRole role;
}
