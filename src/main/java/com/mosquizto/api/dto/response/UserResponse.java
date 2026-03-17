package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class UserResponse implements Serializable {

    private Long id;
    private String fullName;
    private String email;
    private String username;
    private UserStatus status;
    private String role;
    private Date createdAt;
    private Date updatedAt;
}
