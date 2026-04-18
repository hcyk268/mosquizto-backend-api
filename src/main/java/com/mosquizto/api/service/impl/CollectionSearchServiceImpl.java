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
        List<String> filters = new ArrayList<>();
        filters.add("visibility = true");  // chỉ search public
        if (createdByUsername != null) {
            filters.add("createdByUsername = \"" + createdByUsername + "\"");
        }
        return filters.isEmpty() ? null : String.join(" AND ", filters);
    }
    @Override
    public void ReindexAll() {
        configureIndex();

        List<Collection> all = collectionRepository.findAll();
        if (all.isEmpty()) return;

        List<CollectionDocument> docs = all.stream()
                .map(c -> CollectionDocument.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .visibility(c.getVisibility())
                        .createdByUsername(c.getCreatedBy().getUsername())
                        .count(c.getCount())
                        .build())
                .toList();

        meiliClient.index(INDEX)
                .addDocuments(new Gson().toJson(docs), "id");

        log.info("Reindexed {} collections to Meilisearch", docs.size());
    }
}
