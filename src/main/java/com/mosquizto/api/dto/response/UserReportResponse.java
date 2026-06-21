package com.mosquizto.api.dto.response;

import com.mosquizto.api.util.UserReportStatus;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class UserReportResponse implements Serializable {

    private Long id;
    private Long reportedUserId;
    private Long reporterId;
    private String reason;
    private String description;
    private UserReportStatus status;
    private String createAt;
    private String updateAt;
}
