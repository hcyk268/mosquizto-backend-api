package com.mosquizto.api.service;

import com.mosquizto.api.model.StudySession;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class StudySessionStatsCalculator {

    public int getTotalCorect(List<StudySession> sessions) {
        return sessions.stream()
                .mapToInt(session -> session.getTotalCorrect() == null ? 0 : session.getTotalCorrect())
                .sum();
    }

    public int getTotalWrong(List<StudySession> sessions) {
        return sessions.stream()
                .mapToInt(session -> session.getTotalWrong() == null ? 0 : session.getTotalWrong())
                .sum();
    }

    public int getBestScore(List<StudySession> sessions) {
        return sessions.stream()
                .mapToInt(session -> session.getTotalScore() == null ? 0 : session.getTotalScore())
                .max()
                .orElse(0);
    }

    public double getAverageAccuracyRate(List<StudySession> sessions) {
        return sessions.stream()
                .mapToDouble(StudySession::calculateAccuracyRate)
                .average()
                .orElse(0.0);
    }

    public long getAverageDurationMs(List<StudySession> sessions) {
        return (long) sessions.stream()
                .filter(session -> session.getStartedAt() != null && session.isCompleted())
                .mapToLong(StudySession::calculateDurationMs)
                .average()
                .orElse(0.0);
    }

    public Date getLastStudiedAt(List<StudySession> sessions) {
        return sessions.stream()
                .map(StudySession::getStartedAt)
                .filter(java.util.Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);
    }
}
