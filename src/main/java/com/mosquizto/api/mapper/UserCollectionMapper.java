package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.MemberResponse;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserCollection;
import com.mosquizto.api.util.CollectionRole;
import org.springframework.stereotype.Component;

@Component
public class UserCollectionMapper {

    public MemberResponse toOwnerMemberResponse(User user) {
        return toMemberResponse(user, CollectionRole.OWNER);
    }

    public MemberResponse toMemberResponse(UserCollection userCollection) {
        return toMemberResponse(userCollection.getUser(), userCollection.getRole());
    }

    private MemberResponse toMemberResponse(User user, CollectionRole role) {
        return MemberResponse.builder()
                .username(user != null ? user.getUsername() : null)
                .fullname(user != null ? user.getFullName() : null)
                .role(role)
                .build();
    }
}
