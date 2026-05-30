package com.mosquizto.api.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mosquizto.api.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleVerifier {

    @Value("${google.client-id}")
    private String clientId;

    public GoogleIdToken.Payload verify(String idTokenString) {

        NetHttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | java.io.IOException e) {
            throw new InvalidTokenException("Unable to verify Google ID token");
        }

        if (idToken == null) {
            throw new InvalidTokenException("Invalid Google ID token");
        }

        return idToken.getPayload();
    }
}
