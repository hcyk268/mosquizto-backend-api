package com.mosquizto.api.mapper;

import com.mosquizto.api.dto.response.CollectionSummaryResponse;
import com.mosquizto.api.dto.response.FolderResponse;
import com.mosquizto.api.dto.response.FolderSummaryResponse;
import com.mosquizto.api.model.Folder;
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
}
