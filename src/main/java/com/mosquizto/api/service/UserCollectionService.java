package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserCollectionService {
    void shareCollection(Integer collectionId, @Valid ShareCollectionRequest shareCollectionRequest);

    List<MemberResponse> getAllMembersCollection(Integer collectionId);

    void joinCollection(Integer collectionId);

    void deleteCollectionMember(Integer collectionId, Long userId);
}
