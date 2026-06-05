package com.mosquizto.api.event.dto;

public record CollectionSharedEvent(
        String targetEmail,
        String targetUsername,
        String inviterName,
        String collectionTitle,
        String role
) {}
