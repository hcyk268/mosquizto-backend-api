package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.MemberResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UserCollectionService {
    void shareCollection(String token, Integer collectionId, @Valid ShareCollectionRequest shareCollectionRequest);

    List<MemberResponse> getAllMembersCollection(String token, Integer collectionId);

    void joinCollection(String token, Integer collectionId);

    void deleteCollectionMember(String token, Integer collectionId, Long userId);


}
