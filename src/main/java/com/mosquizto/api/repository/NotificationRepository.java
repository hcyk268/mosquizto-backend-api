package com.mosquizto.api.repository;

import com.mosquizto.api.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    @Query("""
        SELECT n FROM Notification n
        WHERE n.recipient.id = :userId
          AND n.read = false
          AND n.deletedAt IS NULL
        ORDER BY n.createdAt DESC
    """)
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
        UPDATE Notification n SET n.read = true, n.readAt = :now
        WHERE n.recipient.id = :userId AND n.read = false
    """)
    void markAllAsReadByUserId(@Param("userId") Long userId, @Param("now") Date now);

    long countByRecipientIdAndReadFalse(Long recipientId);
}
