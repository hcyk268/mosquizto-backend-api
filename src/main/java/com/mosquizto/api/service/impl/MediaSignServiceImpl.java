package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.MediaSignResponse;
import com.mosquizto.api.exception.InvalidDataException;
import com.mosquizto.api.service.MediaSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@RequiredArgsConstructor
@Service
public class MediaSignServiceImpl implements MediaSignService {

    private static final String DEFAULT_FOLDER = "mosquizto/avatars";

    @Value("${cloudinary.name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Override
    public MediaSignResponse signForUser(Long userId, String folder) {
        if (userId == null) {
            throw new InvalidDataException("User id must not be null");
        }

        String targetFolder = (folder == null || folder.isBlank()) ? DEFAULT_FOLDER : folder.trim();
        long timestamp = Instant.now().getEpochSecond();
        String publicId = targetFolder + "/user_" + userId + "_" + timestamp;

        String toSign = "folder=" + targetFolder
                + "&public_id=" + publicId
                + "&timestamp=" + timestamp;

        return MediaSignResponse.builder()
                .cloudName(cloudName)
                .apiKey(apiKey)
                .timestamp(timestamp)
                .signature(sha1Hex(toSign + apiSecret))
                .folder(targetFolder)
                .publicId(publicId)
                .build();
    }

    @Override
    public void validateAvatarUrl(Long userId, String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            throw new InvalidDataException("Avatar URL must not be blank");
        }

        String expectedPrefix = "https://res.cloudinary.com/" + cloudName + "/";
        if (!avatarUrl.startsWith(expectedPrefix)) {
            throw new InvalidDataException("Avatar URL must be hosted on Cloudinary");
        }

        String userPattern = "user_" + userId + "_";
        if (!avatarUrl.contains(userPattern)) {
            throw new InvalidDataException("Avatar URL does not belong to this user");
        }
    }

    private static String sha1Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
