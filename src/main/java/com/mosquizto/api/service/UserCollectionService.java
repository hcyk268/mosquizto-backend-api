package com.mosquizto.api.service;

import com.mosquizto.api.dto.request.ShareCollectionRequest;
import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.dto.response.ShareCollectionResponse;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.util.AccessStatus;
import com.mosquizto.api.util.CollectionRole;
import jakarta.validation.Valid;

import java.util.List;

public interface UserCollectionService {
    void shareCollection(Integer collectionId, @Valid ShareCollectionRequest shareCollectionRequest);

    List<MemberResponse> getAllMembersCollection(Integer collectionId);

    void joinCollection(Integer collectionId);

    void deleteCollectionMember(Integer collectionId, Long userId);

    void approveJoinRequest(Integer collectionId, Long userId, AccessStatus status);

    void updateLastOpenedAt(Long userId,Integer collectionId);
    void removeRecentOpenedCollection(Integer collectionId);

    // Lấy danh sách lời mời
    List<ShareCollectionResponse> getMyPendingInvitations();

    // Người được mời phản hồi (Accept hoặc Deny)
    void respondToShareInvite(Integer collectionId, AccessStatus status);

    CollectionRole getRoleOfCollection(Integer collectionId);
}

