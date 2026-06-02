package com.mosquizto.api.service.impl;

import com.mosquizto.api.model.Collection;
import com.mosquizto.api.service.VectorStoreService;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.JsonWithInt.Value; // Corrected Import
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.qdrant.client.ValueFactory.value; // Recommended for clean payloads

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements VectorStoreService {
    private final QdrantClient qdrantClient;
    private static final String COLLECTION_NAME = "mosquizto_collections";
    private static final int VECTOR_SIZE = 384;

    @Override
    @PostConstruct
    public void ensureCollection() {
        try {
            qdrantClient.getCollectionInfoAsync(COLLECTION_NAME).get();
        } catch (Exception e) {
            try {
                qdrantClient.createCollectionAsync(
                        COLLECTION_NAME,
                        Collections.VectorParams.newBuilder()
                                .setSize(VECTOR_SIZE)
                                .setDistance(Collections.Distance.Cosine)
                                .build()
                ).get();
            } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
                // Best practice: Restore the interrupted status
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                // Throw as a runtime exception so Spring knows initialization failed
                throw new RuntimeException("Failed to create Qdrant collection", ex);
            }
        }

    }

    @Override
    public void upsertCollection(Collection col, float[] vector) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("BÁO ĐỘNG: Vector của Collection ID " + col.getId() + " bị rỗng! Dừng lưu Qdrant.");
        }
        // Drastically simplified using ValueFactory.value()
        Map<String, Value> payload = Map.of(
                "collectionId", value(col.getId()),
                "description", value(col.getDescription()),
                "title", value(col.getTitle()),
                "owner", value(col.getCreatedBy().getUsername()),
                "isPublic", value(col.getVisibility())
        );

        Points.PointStruct point = Points.PointStruct.newBuilder()
                .setId(Points.PointId.newBuilder().setNum(col.getId()).build())
                .setVectors(Points.Vectors.newBuilder()
                        .setVector(Points.Vector.newBuilder()
                                .addAllData(toFloatList(vector)).build())
                        .build())
                .putAllPayload(payload)
                .build();

        try {
            // THÊM .get() Ở ĐÂY ĐỂ CHỜ KẾT QUẢ VÀ BẮT LỖI
            qdrantClient.upsertAsync(COLLECTION_NAME, List.of(point)).get();
        } catch (Exception e) {
            log.error("Failed to upsert vector for collection ID: {}", col.getId(), e);
        }
    }

    @Override
    public List<Collection> getEmbeddedCollections() {
        //qdrantClient.getCollectionInfoAsync(COLLECTION_NAME).get().
        return null ;
    }


    private List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) list.add(f);
        return list;
    }
}