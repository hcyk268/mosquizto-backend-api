package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.CollectionRole;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class MemberResponse implements Serializable {
    private String username;
    private String fullname;
    private CollectionRole role;
}
