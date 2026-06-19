package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.CollectionSummaryResponse;
import com.mosquizto.api.dto.response.FolderMemberResponse;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import com.mosquizto.api.model.Folder;
import com.mosquizto.api.model.User;
import com.mosquizto.api.model.UserFolder;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class FolderMapper {

    public FolderResponse toFolderResponse(Folder folder) {
        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .description(folder.getDescription())
                .collections(
                        folder.getFolderCollections().stream()
                                .sorted(Comparator.comparing(
                                        folderCollection -> folderCollection.getOrderIndex(),
                                        Comparator.nullsLast(Integer::compareTo)))
                                .map(folderCollection -> CollectionSummaryResponse.builder()
                                                .id(folderCollection.getCollection().getId())
                                                .title(folderCollection.getCollection().getTitle())
                                                .orderIndex(folderCollection.getOrderIndex())
                                                .build()
                                ).toList()
                )
                .build();
    }

    public FolderSummaryResponse toFolderSummaryResponse(Folder folder) {
        return FolderSummaryResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .build();
    }

    public FolderMemberResponse toFolderMemberResponse(UserFolder membership) {
        User user = membership.getUser();
        return FolderMemberResponse.builder()
                .userId(user != null ? user.getId() : null)
                .username(user != null ? user.getUsername() : null)
                .fullName(user != null ? user.getFullName() : null)
                .imgUri(user != null ? user.getAvatarUrl() : null)
                .role(membership.getRole())
                .build();
    }
}
