package com.mosquizto.api.event.listener;
import com.mosquizto.api.event.dto.CollectionReportEvent;
import com.mosquizto.api.event.dto.CollectionSharedEvent;
import com.mosquizto.api.service.MailService;
import com.mosquizto.api.service.NotificationService;
import com.mosquizto.api.util.NotificationWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectionEventListener {
    private final MailService mailService;
    private final NotificationService notificationService;

    // Lắng nghe sự kiện. Dùng @Async để nó chạy ngầm (background thread), ko làm chậm API trả về cho FE
    @Async
    @EventListener
    public void handleCollectionShared(CollectionSharedEvent event) {
        // Đệ 1: Đi gửi mail
        mailService.sendCollectionShareInvite(
                event.targetEmail(), event.targetUsername(), event.inviterName(), event.collectionTitle(), event.role()
        );

        // Đệ 2: Đi gửi Websocket
        notificationService.sendInvitationToSpecificUser(
                event.targetUsername(),
                NotificationWriter.inviteToCollection(event.inviterName(), event.role(), event.collectionTitle())
        );
    }
    @Async
    @EventListener
    public void handleCollectionReported(CollectionReportEvent event)
    {
        mailService.sendCollectionReportNotification(event.targetMail(),event.targetUsername(), event.reporterName(),
                event.collectionTitle(), event.reason(), event.description());
        notificationService.sendReportToSpecificUser(event.targetUsername(),
                NotificationWriter.reportCollection(event.collectionTitle(),event.reporterName()));
    }
}
