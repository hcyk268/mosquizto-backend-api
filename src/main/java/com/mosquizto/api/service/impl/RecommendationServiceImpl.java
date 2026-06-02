package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.CollectionResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.mapper.CollectionMapper;
import com.mosquizto.api.model.Collection;
import com.mosquizto.api.repository.CollectionRepository;
import com.mosquizto.api.service.*;
import com.mosquizto.api.util.CentroidCalculator;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final QdrantClient qdrantClient;
    private final CollectionService collectionService;
    private final CentroidCalculator centroidCalculator;
    private final CollectionRepository collectionRepository;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    private static final String COLLECTION_NAME = "mosquizto_collections";
    private static final int RESULT_LIMIT = 10;
    private static final String TAG = "[RECOMMENDATION-SERVICE]";

    @Override
    public PageResponse<CollectionResponse> recommendBaseOnRecent(int page, int size) throws ExecutionException, InterruptedException {
        log.info("{} 🟢 Bắt đầu quy trình gợi ý cho Page: {}, Size: {}", TAG, page, size);

        // 1. Lấy danh sách ID từ Qdrant
        List<String> recommendedIdsStr = getRecommendedCollectionId(page, size);
        log.info("{} 🔍 Qdrant trả về {} ID gợi ý: {}", TAG, recommendedIdsStr.size(), recommendedIdsStr);

        if (recommendedIdsStr.isEmpty()) {
            log.warn("{} ⚠️ Không tìm thấy gợi ý nào từ Qdrant, trả về trang trống.", TAG);
            return PageResponse.<CollectionResponse>builder()
                    .page(page).size(size).totalElements(0).totalPages(0).items(List.of()).build();
        }

        // 2. Chuyển đổi ID
        List<Integer> recommendedIds = recommendedIdsStr.stream()
                .map(Integer::valueOf)
                .toList();

        // 3. Fetch data từ DB
        log.info("{} 💾 Đang fetch dữ liệu chi tiết từ PostgreSQL cho {} IDs...", TAG, recommendedIds.size());
        List<Collection> collectionsFromDb = collectionService.getCollectionsByIdsIn(recommendedIdsStr);
        log.info("{} ✅ Đã lấy được {}/{} records từ DB.", TAG, collectionsFromDb.size(), recommendedIds.size());

        CollectionMapper mapper = new CollectionMapper();
        Map<Integer, Collection> collectionMap = collectionsFromDb.stream()
                .collect(Collectors.toMap(Collection::getId, c -> c));

        // 4. Sắp xếp và Map sang DTO
        List<CollectionResponse> sortedRankedItems = recommendedIds.stream()
                .filter(collectionMap::containsKey)
                .map(id -> mapper.toResponse(collectionMap.get(id)))
                .toList();

        log.info("{} 🏆 Hoàn thành quy trình gợi ý. Trả về {} items.", TAG, sortedRankedItems.size());

        return PageResponse.<CollectionResponse>builder()
                .page(page)
                .size(size)
                .totalElements(sortedRankedItems.size())
                .totalPages(1)
                .items(sortedRankedItems)
                .build();
    }

    @Override
    public void syncAllCollectionsToQdrant() throws ExecutionException, InterruptedException {
        log.info("{} 🔄 [SYNC] Bắt đầu đồng bộ toàn bộ Collection sang Qdrant...", TAG);
        List<Collection> allCollections = collectionRepository.findAll();
        log.info("{} 🔄 [SYNC] Tìm thấy {} collections trong PostgreSQL.", TAG, allCollections.size());

        int successCount = 0;
        for (Collection col : allCollections) {
            try {
                float[] vector = embeddingService.embedCollection(col);
                vectorStoreService.upsertCollection(col, vector);
                successCount++;
            } catch (Exception e) {
                log.error("{} ❌ [SYNC] Lỗi tại ID {}: {}", TAG, col.getId(), e.getMessage());
            }
        }
        log.info("{} ✅ [SYNC] Hoàn tất! Đã đồng bộ thành công {}/{} collections.", TAG, successCount, allCollections.size());
        try
        {
            var points = qdrantClient.retrieveAsync(
                    COLLECTION_NAME,
                    List.of(
                            Points.PointId.newBuilder().setNum(1).build()
                    ),
                    null,
                    Points.WithVectorsSelector.newBuilder()
                            .setEnable(true)
                            .build(),
                    null
            ).get();

            System.out.println(points);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    public List<String> getRecommendedCollectionId(int page, int size) throws ExecutionException, InterruptedException {
        // Lấy danh sách đã xem
        var recentOpened = collectionService.getRecentOpenedCollection();
        log.info("{} 🕒 Recent Opened từ DB: {} collections.", TAG, recentOpened.size());

        List<String> recentIdsStr = recentOpened.stream()
                .limit(RESULT_LIMIT)
                .map(res -> res.getId().toString())
                .toList();

        if (recentIdsStr.isEmpty()) {
            log.warn("{} ⚠️ Danh sách Recent rỗng. User chưa xem collection nào.", TAG);
            return List.of();
        }

        List<Points.PointId> pointIds = recentIdsStr.stream()
                .map(id -> Points.PointId.newBuilder().setNum(Integer.valueOf(id)).build())
                .toList();

        // Lấy vector từ Qdrant
        log.info("{} 🔌 Đang truy xuất vectors từ Qdrant cho các IDs: {}", TAG, recentIdsStr);
        Points.Filter idFilter = Points.Filter.newBuilder()
                .addMust(Points.Condition.newBuilder()
                        .setHasId(Points.HasIdCondition.newBuilder().addAllHasId(pointIds)))
                .build();
        var retrievedPoints = qdrantClient.retrieveAsync(
                COLLECTION_NAME,
                pointIds,
                Points.WithPayloadSelector.newBuilder()
                        .setEnable(false)
                        .build(),
                Points.WithVectorsSelector.newBuilder()
                        .setEnable(true)
                        .build(),
                null
        ).get();
        log.info("Retrieved size = {}", retrievedPoints.size());

        for (var p : retrievedPoints) {
            log.info(
                    "ID={} vectorSize={}",
                    p.getId().getNum(),
                    p.getVectors().getVector().getDataCount()
            );
        }
        // Lọc vector hợp lệ
        List<float[]> recentVectors = retrievedPoints.stream()
                .map(rp -> {
                    var dataList = rp.getVectors().getVector().getDataList();
                    if (dataList == null || dataList.isEmpty()) {
                        return new float[0];
                    }
                    float[] arr = new float[dataList.size()];
                    for (int i = 0; i < dataList.size(); i++) {
                        arr[i] = dataList.get(i).floatValue();
                    }
                    return arr;
                })
                .filter(arr -> arr.length == 384)
                .toList();
        log.info("{} 📊 Đã lấy được {} vectors hợp lệ (384 dims) từ Qdrant.", TAG, recentVectors.size());

        if (recentVectors.isEmpty()) {
            log.error("{} ❌ KHÔNG CÓ VECTOR HỢP LỆ. Có thể do data chưa được Sync hoặc chưa Embed.", TAG);
            return List.of();
        }

        float[] centroid = centroidCalculator.compute(recentVectors);
        int offset = page * size;

        log.info("{} 🔎 Đang thực hiện Vector Search (Cosine Similarity) với Offset: {}", TAG, offset);
        List<Points.ScoredPoint> results = qdrantClient.searchAsync(
                Points.SearchPoints.newBuilder()
                        .setCollectionName(COLLECTION_NAME)
                        .addAllVector(toFloatList(centroid))
                        .setLimit(size)
                        .setOffset(offset)
                        .setWithPayload(Points.WithPayloadSelector.newBuilder().setEnable(true).build())
                        .setFilter(Points.Filter.newBuilder()
                                .addMust(Points.Condition.newBuilder()
                                        .setField(Points.FieldCondition.newBuilder()
                                                .setKey("isPublic")
                                                .setMatch(Points.Match.newBuilder().setBoolean(true).build()).build()).build())
                                .addMustNot(Points.Condition.newBuilder()
                                        .setHasId(Points.HasIdCondition.newBuilder().addAllHasId(pointIds).build()).build())
                                .build())
                        .build()
        ).get();

        return results.stream()
                .map(p -> p.getPayloadOrDefault("collectionId", null).getStringValue())
                .toList();
    }

    private List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) list.add(f);
        return list;
    }
}