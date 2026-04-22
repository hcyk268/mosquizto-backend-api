package com.mosquizto.api.service.impl;

import com.google.gson.Gson;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.SearchResultPaginated;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.model.CollectionDocument;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.CollectionSearchService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionSearchServiceImpl implements CollectionSearchService {

    private final Client meiliClient;
    private static final String INDEX = "collections";
    private  final CollectionRepository collectionRepository ;
    @Override
    @PostConstruct
    public void configureIndex() {
        Index index = meiliClient.index(INDEX);

        // Các field được search full-text
        index.updateSearchableAttributesSettings(
                new String[]{"title", "description"}
        );

        // Các field dùng để filter
        index.updateFilterableAttributesSettings(
                new String[]{"visibility", "createdByUsername", "count"}
        );

        // Sort
        index.updateSortableAttributesSettings(
                new String[]{"count"}
        );
    }

    @Override
    public void upsert(Collection collection) {
        CollectionDocument doc = CollectionDocument.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .visibility(collection.getVisibility())
                .createdByUsername(collection.getCreatedBy().getUsername())
                .count(collection.getCount())
                .build();

        meiliClient.index(INDEX).addDocuments(new Gson().toJson(List.of(doc)), "id");
    }


    @Override
    public void delete(Integer id) {
        meiliClient.index(INDEX).deleteDocument(String.valueOf(id));
    }

    @Override
    public SearchResultPaginated search(String query, int page, int pageSize, String createdByUsername) {
        SearchRequest request = SearchRequest.builder()
                .q(query)
                .page(page)
                .hitsPerPage(pageSize)
                .filter(new String[]{buildFilter(createdByUsername)})
                .build();
        return (SearchResultPaginated ) meiliClient.index(INDEX).search(request);
    }
    private String buildFilter(String createdByUsername) {
        if (createdByUsername != null) {
            return "(visibility = true) OR (createdByUsername = \"" + createdByUsername + "\")";
        }
        return "visibility = true";
    }
    @Override
    public void ReindexAll() {
        log.info("Starting lazy reindex for Meilisearch...");
        configureIndex();

        int page = 0;
        int pageSize = 500;
        long totalIndexed = 0;
        org.springframework.data.domain.Page<Collection> collectionPage;

        do {
            collectionPage = collectionRepository.findAll(org.springframework.data.domain.PageRequest.of(page, pageSize));

            if (collectionPage.hasContent()) {
                List<CollectionDocument> docs = collectionPage.getContent().stream()
                        .map(c -> CollectionDocument.builder()
                                .id(c.getId())
                                .title(c.getTitle())
                                .description(c.getDescription())
                                .visibility(c.getVisibility())
                                .createdByUsername(c.getCreatedBy().getUsername())
                                .count(c.getCount())
                                .build())
                        .toList();
                meiliClient.index(INDEX).addDocuments(new Gson().toJson(docs), "id");

                totalIndexed += docs.size();
                log.info("Indexed batch: page {}, size {}", page, docs.size());
            }

            page++;
        } while (collectionPage.hasNext());

        log.info("Total reindexed {} collections to Meilisearch", totalIndexed);
    }
}
