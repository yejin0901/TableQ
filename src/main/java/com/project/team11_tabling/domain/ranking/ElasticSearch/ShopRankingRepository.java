package com.project.team11_tabling.domain.ranking.ElasticSearch;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ShopRankingRepository extends ElasticsearchRepository<ShopAnalysis, String> {
  Page<ShopAnalysis> findAllByOrderByReservationCountDesc(Pageable pageable);
  Page<ShopAnalysis> findAllByOrderByReviewCountDesc(Pageable pageable);

  @Query("{\"bool\": {\"must\": [{\"match\": {\"area\": \"?0\"}}],\"must_not\": [],\"should\": []}}")
  Page<ShopAnalysis> findTop5ByAreaOrderByReservationCountDesc(String area, PageRequest pageRequest);
}