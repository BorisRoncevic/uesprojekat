package com.app.godo;

import com.app.godo.services.files.MinIOService;
import com.app.godo.repositories.venue.VenueESRepository;
import com.app.godo.utils.ElasticSearchIndexInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GoDoApplicationTests {

    @MockBean
    private ElasticSearchIndexInitializer elasticSearchIndexInitializer;

    @MockBean
    private MinIOService minIOService;

    @MockBean
    private VenueESRepository venueESRepository;

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    void contextLoads() {
    }

}
