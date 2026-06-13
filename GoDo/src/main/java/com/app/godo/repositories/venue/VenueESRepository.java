package com.app.godo.repositories.venue;

import com.app.godo.models.VenueDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VenueESRepository extends ElasticsearchRepository<VenueDocument, Long> {

}
