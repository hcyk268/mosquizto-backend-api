package com.mosquizto.api.dto.response;

import com.mosquizto.api.model.Notification;
import com.mosquizto.api.util.NotificationType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id ;
    private String recipientName ;
    private Long recipientId ;
    private NotificationType notificationType ;
    private String message ;
    private Boolean read ;
    private LocalDateTime readAt ;
    private Long referenceId ;

    public static NotificationResponse form(Notification notification)
    {
        return NotificationResponse.builder().recipientName(notification.getRecipient().getUsername())
                .recipientId(notification.getRecipient().getId()).notificationType(notification.getType())
                .message(notification.getMessage()).read(notification.isRead()).readAt(notification.getReadAt())
                .referenceId(notification.getReferenceId()).id(notification.getId()).build();

    }
}
