package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.*;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionItem;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollectionItemStar;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserSummaryResponse toSummaryResponse(User user) {
        return this.toSummaryResponse(user, false, 0, 0);
    }

    public UserSummaryResponse toSummaryResponse(User user, boolean followed) {
        return this.toSummaryResponse(user, followed, 0, 0);
    }

    public UserSummaryResponse toSummaryResponse(User user,
                                                 boolean followed,
                                                 long followersCount,
                                                 long followingCount) {
        return UserSummaryResponse.builder()
                .fullName(user.getFullName())
                .username(user.getUsername())
                .followed(followed)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .build();
    }

    public UserStreakResponse toStreakResponse(int currentStreak,
                                               int longestStreak,
                                               int totalStudyDays,
                                               long totalStudySessions,
                                               long completedStudySessions,
                                               Date lastStudiedAt,
                                               boolean studiedToday,
                                               Integer nextMilestoneDays) {
        return UserStreakResponse.builder()
                .currentStreakDays(currentStreak)
                .longestStreakDays(longestStreak)
                .totalStudyDays(totalStudyDays)
                .totalStudySessions(totalStudySessions)
                .completedStudySessions(completedStudySessions)
                .lastStudiedAt(lastStudiedAt)
                .studiedToday(studiedToday)
                .nextMilestoneDays(nextMilestoneDays)
                .build();
    }

    public UserAchievementResponse toAchievementResponse(String code,
                                                         String title,
                                                         String description,
                                                         long progress,
                                                         long target) {
        return UserAchievementResponse.builder()
                .code(code)
                .title(title)
                .description(description)
                .achieved(progress >= target)
                .progress(Math.min(progress, target))
                .target(target)
                .build();
    }

    public UserActivityResponse toStudySessionActivityResponse(StudySession session, Date occurredAt) {
        Collection collection = session.getCollection();
        boolean completed = session.getCompletedAt() != null;
        return UserActivityResponse.builder()
                .type(completed ? "STUDY_SESSION_COMPLETED" : "STUDY_SESSION_STARTED")
                .title(completed ? "Completed a study session" : "Started a study session")
                .description(collection != null ? collection.getTitle() : null)
                .occurredAt(occurredAt)
                .sessionId(session.getId())
                .collectionId(collection != null ? collection.getId() : null)
                .collectionName(collection != null ? collection.getTitle() : null)
                .build();
    }

    public UserActivityResponse toCollectionCreatedActivityResponse(Collection collection) {
        return UserActivityResponse.builder()
                .type("COLLECTION_CREATED")
                .title("Created a flashcard set")
                .description(collection.getTitle())
                .occurredAt(collection.getCreatedAt())
                .collectionId(collection.getId())
                .collectionName(collection.getTitle())
                .build();
    }

    public UserActivityResponse toStarActivityResponse(UserCollectionItemStar star) {
        CollectionItem item = star.getCollectionItem();
        Collection collection = item != null ? item.getCollection() : null;
        return UserActivityResponse.builder()
                .type("ITEM_STARRED")
                .title("Starred a flashcard")
                .description(item != null ? item.getTerm() : null)
                .occurredAt(star.getCreatedAt())
                .itemId(item != null ? item.getId() : null)
                .collectionId(collection != null ? collection.getId() : null)
                .collectionName(collection != null ? collection.getTitle() : null)
                .build();
    }
}
