package com.mosquizto.api;

import com.mosquizto.api.service.CollectionSearchService;
import com.mosquizto.api.service.EmbeddingService;
import com.mosquizto.api.service.VectorStoreService;
import io.qdrant.client.QdrantClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MosquiztoBackendApplicationTests {

	@MockBean
	private CollectionSearchService collectionSearchService;

	@MockBean
	private EmbeddingService embeddingService;

	@MockBean
	private VectorStoreService vectorStoreService;

	@MockBean
	private QdrantClient qdrantClient;

	@Test
	void contextLoads() {
	}

}
