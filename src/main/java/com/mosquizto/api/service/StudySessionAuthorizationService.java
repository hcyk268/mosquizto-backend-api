package com.mosquizto.api.service;

import com.mosquizto.api.model.StudySession;
import org.springframework.stereotype.Component;

@Component
public class StudySessionAuthorizationService {

    public boolean isAuthor(StudySession studySession, String username) {
        return studySession.getUser().getUsername().equals(username);
    }
}
