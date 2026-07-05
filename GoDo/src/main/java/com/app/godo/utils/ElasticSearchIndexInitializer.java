package com.app.godo.utils;

import com.app.godo.models.VenueDocument;
import com.app.godo.services.venue.VenueService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;
    private final VenueService venueService;

    @PostConstruct
    public void ensureIndexExists() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(VenueDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping();
        }

        venueService.syncAllVenuesToElasticsearch();
    }
}
