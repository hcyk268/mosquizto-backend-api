package com.mosquizto.api.controller;
import com.mosquizto.api.dto.response.NotificationResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseData<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<NotificationResponse> response = notificationService.getMyNotifications(page, size);
        return new ResponseData<>(HttpStatus.OK.value(), "success", response) ;
    }

    @GetMapping("/unread-count")
    public ResponseData<Long> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return new ResponseData<>(HttpStatus.OK.value(), "success", count) ;
    }

    @PatchMapping("/{id}/read")
    public ResponseData<Void> markAsRead(@PathVariable Long id ) {
        notificationService.markAsRead(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success") ;
    }

//    @PostMapping("/read-all")
//    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
//        notificationService.markAllAsRead();
//        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
//    }
}