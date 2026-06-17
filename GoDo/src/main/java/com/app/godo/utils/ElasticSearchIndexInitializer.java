package com.app.godo.utils;

import com.app.godo.models.VenueDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void ensureIndexExists() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(VenueDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping();
        }
    }
}