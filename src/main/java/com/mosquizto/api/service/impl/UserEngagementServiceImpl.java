package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserAchievementResponse;
import com.mosquizto.api.dto.response.UserActivityResponse;
import com.mosquizto.api.dto.response.UserStreakResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.mapper.UserMapper;
import com.mosquizto.api.model.StudySession;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.repository.StudySessionRepository;
import com.mosquizto.api.repository.UserCollectionItemStarRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.UserEngagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@RequiredArgsConstructor
@Service
public class UserEngagementServiceImpl implements UserEngagementService {

    private static final int[] STREAK_MILESTONES = {1, 3, 7, 14, 30, 60, 100};

    private final CurrentUserProvider currentUserProvider;
    private final StudySessionRepository studySessionRepository;
    private final CollectionRepository collectionRepository;
    private final UserCollectionItemStarRepository starRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserStreakResponse getStreak() {
        User user = this.currentUserProvider.getCurrentUser();

        List<StudySession> sessions = this.studySessionRepository.findAllByUserIdOrderByStartedAtDesc(user.getId());

        TreeSet<LocalDate> studyDates = getStudyDatesDesc(sessions);

        int currentStreak = calculateCurrentStreak(studyDates);
        int longestStreak = calculateLongestStreak(studyDates);

        Date lastStudiedAt = sessions.stream()
                .map(this::activityDate)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        return this.userMapper.toStreakResponse(
                currentStreak,
                longestStreak,
                studyDates.size(),
                this.studySessionRepository.countByUserId(user.getId()),
                this.studySessionRepository.countByUserIdAndCompletedAtIsNotNull(user.getId()),
                lastStudiedAt,
                studyDates.contains(today),
                nextStreakMilestone(currentStreak)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> getAchievements() {
        User user = this.currentUserProvider.getCurrentUser();
        List<StudySession> sessions = this.studySessionRepository.findAllByUserIdOrderByStartedAtDesc(user.getId());
        TreeSet<LocalDate> studyDates = getStudyDatesDesc(sessions);

        long totalStudySessions = this.studySessionRepository.countByUserId(user.getId());
        long createdCollections = this.collectionRepository.countByCreatedById(user.getId());
        long starredItems = this.starRepository.countByUserId(user.getId());
        long totalCorrect = this.studySessionRepository.sumTotalCorrectByUserId(user.getId());
        boolean hasPerfectSession = this.studySessionRepository.existsPerfectSessionByUserId(user.getId());
        int longestStreak = calculateLongestStreak(studyDates);

        return List.of(
                this.userMapper.toAchievementResponse("FIRST_STUDY", "First study session",
                        "Start your first study session.", totalStudySessions, 1),
                this.userMapper.toAchievementResponse("STREAK_3", "3-day streak",
                        "Study on 3 consecutive days.", longestStreak, 3),
                this.userMapper.toAchievementResponse("STREAK_7", "7-day streak",
                        "Study on 7 consecutive days.", longestStreak, 7),
                this.userMapper.toAchievementResponse("TEN_SESSIONS", "10 study sessions",
                        "Start 10 study sessions.", totalStudySessions, 10),
                this.userMapper.toAchievementResponse("FIRST_SET", "Create a set",
                        "Create your first flashcard set.", createdCollections, 1),
                this.userMapper.toAchievementResponse("STAR_COLLECTOR", "Star collector",
                        "Star 10 useful flashcards.", starredItems, 10),
                this.userMapper.toAchievementResponse("FIFTY_CORRECT", "50 correct answers",
                        "Answer 50 cards correctly.", totalCorrect, 50),
                this.userMapper.toAchievementResponse("PERFECT_SESSION", "Perfect session",
                        "Finish a session without a wrong answer.", hasPerfectSession ? 1 : 0, 1)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserActivityResponse> getActivity(int page, int size) {
        if (page < 1) {
            throw new InvalidDataException("Page must be greater than or equal to 1");
        }

        if (size < 1) {
            throw new InvalidDataException("Size must be greater than or equal to 1");
        }

        User user = this.currentUserProvider.getCurrentUser();
        int fetchSize = page * size;
        List<UserActivityResponse> activities = new ArrayList<>();

        this.studySessionRepository
                .findAllByUserId(user.getId(), PageRequest.of(0, fetchSize, Sort.by(Sort.Direction.DESC, "startedAt")))
                .getContent()
                .forEach(session -> activities.add(
                        this.userMapper.toStudySessionActivityResponse(session, activityDate(session))));

        this.collectionRepository
                .findAllByCreatedById(user.getId(), PageRequest.of(0, fetchSize, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent()
                .forEach(collection -> activities.add(this.userMapper.toCollectionCreatedActivityResponse(collection)));

        this.starRepository
                .findRecentByUserId(user.getId(), PageRequest.of(0, fetchSize))
                .forEach(star -> activities.add(this.userMapper.toStarActivityResponse(star)));

        activities.sort(this::compareActivityDesc);

        int offset = (page - 1) * size;
        int fromIndex = Math.min(offset, activities.size());
        int toIndex = Math.min(fromIndex + size, activities.size());
        List<UserActivityResponse> items = activities.subList(fromIndex, toIndex);

        long totalElements = this.studySessionRepository.countByUserId(user.getId())
                + this.collectionRepository.countByCreatedById(user.getId())
                + this.starRepository.countByUserId(user.getId());

        return PageResponse.<UserActivityResponse>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(size == 0 ? 0 : (int) Math.ceil((double) totalElements / size))
                .items(items)
                .build();
    }

    private TreeSet<LocalDate> getStudyDatesDesc(List<StudySession> sessions) {
        TreeSet<LocalDate> studyDates = new TreeSet<>(Comparator.reverseOrder());
        ZoneId zoneId = ZoneId.systemDefault();
        sessions.stream()
                .map(this::activityDate)
                .filter(Objects::nonNull)
                .map(date -> date.toInstant().atZone(zoneId).toLocalDate())
                .forEach(studyDates::add);
        return studyDates;
    }

    private int calculateCurrentStreak(TreeSet<LocalDate> studyDates) {
        if (studyDates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate latestStudyDate = studyDates.first();
        if (latestStudyDate.isBefore(today.minusDays(1))) {
            return 0;
        }

        int streak = 0;
        LocalDate expected = latestStudyDate;
        for (LocalDate studyDate : studyDates) {
            if (!studyDate.equals(expected)) {
                break;
            }

            streak++;
            expected = expected.minusDays(1);
        }

        return streak;
    }

    private int calculateLongestStreak(TreeSet<LocalDate> studyDatesDesc) {
        int longest = 0;
        int current = 0;
        LocalDate previous = null;

        for (LocalDate studyDate : studyDatesDesc.descendingSet()) {
            if (previous == null || studyDate.equals(previous.plusDays(1))) {
                current++;
            } else {
                current = 1;
            }

            longest = Math.max(longest, current);
            previous = studyDate;
        }

        return longest;
    }

    private Integer nextStreakMilestone(int currentStreak) {
        for (int milestone : STREAK_MILESTONES) {
            if (currentStreak < milestone) {
                return milestone;
            }
        }

        return null;
    }

    private int compareActivityDesc(UserActivityResponse left, UserActivityResponse right) {
        Date leftDate = left.getOccurredAt();
        Date rightDate = right.getOccurredAt();

        if (leftDate == null && rightDate == null) {
            return 0;
        }

        if (leftDate == null) {
            return 1;
        }

        if (rightDate == null) {
            return -1;
        }

        return rightDate.compareTo(leftDate);
    }

    private Date activityDate(StudySession session) {
        if (session.getCompletedAt() != null) {
            return session.getCompletedAt();
        }

        if (session.getStartedAt() != null) {
            return session.getStartedAt();
        }

        return session.getCreatedAt();
    }
}
