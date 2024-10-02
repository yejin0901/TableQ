package com.project.team11_tabling.domain.ranking.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ScriptType;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopRankingService {
  private final ShopRankingRepository shopRankingRepository;
  private final ElasticsearchOperations elasticsearchOperations;
  private final ElasticsearchTemplate elasticsearchTemplate;
  private final ElasticsearchClient client;

  public void incrementViewCount(String documentId, String indexName) {
    // 스크립트로 업데이트를 실행합니다.
    String script = "ctx._source.viewCount += 1";

    UpdateQuery updateQuery = UpdateQuery.builder(documentId)
        .withScript(script)
        .withScriptType(ScriptType.INLINE)
        .withIndex(indexName)
        .build();

    try {
      elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("shops"));
    } catch (Exception e) {
      System.err.println("Error updating document: " + e.getMessage());
    }

  }

  public void incrementWaitCount(String documentId, String indexName) {
    // 스크립트로 업데이트를 실행합니다.
    String script = "ctx._source.waitCount += 1";

    UpdateQuery updateQuery = UpdateQuery.builder(documentId)
            .withScript(script)
            .withScriptType(ScriptType.INLINE)
            .withIndex(indexName)
            .build();

    try {
      elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("shops"));
    } catch (Exception e) {
      System.err.println("Error updating document: " + e.getMessage());
    }

  }

  public void incrementReservationCount(String documentId, String indexName) {
    // 스크립트로 업데이트를 실행합니다.
    String script = "ctx._source.reservationCount += 1";

    UpdateQuery updateQuery = UpdateQuery.builder(documentId)
        .withScript(script)
        .withScriptType(ScriptType.INLINE)
        .withIndex(indexName)
        .build();

    try {
      elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("shops"));
    } catch (Exception e) {
      System.err.println("Error updating document: " + e.getMessage());
    }

  }

  public void incrementReviewCount(String documentId, String indexName) {
    // 스크립트로 업데이트를 실행합니다.
    String script = "ctx._source.reviewCount += 1";

    UpdateQuery updateQuery = UpdateQuery.builder(documentId)
        .withScript(script)
        .withScriptType(ScriptType.INLINE)
        .withIndex(indexName)
        .build();

    try {
      elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("shops"));
    } catch (Exception e) {
      System.err.println("Error updating document: " + e.getMessage());
    }

  }

  public void incrementCancelCount(String documentId, String indexName) {
    // 스크립트로 업데이트를 실행합니다.
    String script = "ctx._source.cancelCount += 1";

    UpdateQuery updateQuery = UpdateQuery.builder(documentId)
        .withScript(script)
        .withScriptType(ScriptType.INLINE)
        .withIndex(indexName)
        .build();

    try {
      elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("shops"));
    } catch (Exception e) {
      System.err.println("Error updating document: " + e.getMessage());
    }

    incrementCancelRate(documentId, indexName);
  }

  public void incrementCancelRate(String documentId, String indexName) {
    // 스크립트로 업데이트를 실행합니다.
    Double reservationCount = shopRankingRepository.findById(documentId);
    String script = "ctx._source.cancelRate = cancelCount/" + reservationCount;

    UpdateQuery updateQuery = UpdateQuery.builder(documentId)
        .withScript(script)
        .withScriptType(ScriptType.INLINE)
        .withIndex(indexName)
        .build();

    try {
      elasticsearchTemplate.update(updateQuery, IndexCoordinates.of("shops"));
    } catch (Exception e) {
      System.err.println("Error updating document: " + e.getMessage());
    }
  }

  public Map<String, Set<String>> findTopShops() throws Exception {
    TermsAggregation aggregation = TermsAggregation.of(t -> t
        .field("area.keyword")
    );

    Aggregation aggs = Aggregation.of(s -> s
        .terms(aggregation)
        .aggregations("result", r -> r.topHits(createTopHitsAggregation()))
    );

    SearchRequest searchRequest = new SearchRequest.Builder()
        .index("shops")
        .aggregations("aggs", aggs)
        .size(10)
        .build();

    SearchResponse<Void> searchResponse = client.search(searchRequest, Void.class);
    log.info(searchResponse.toString());
    Map<String, Set<String>> topShops = new HashMap<>();

    StringTermsAggregate aggregate = searchResponse.aggregations().get("aggs").sterms();
    List<StringTermsBucket> buckets = aggregate.buckets().array();

    for (StringTermsBucket bucket : buckets) {
      TopHitsAggregate topHits = bucket.aggregations().get("result").topHits();
      String area = bucket.key().stringValue();
      Set<String> shops = topShops.getOrDefault(area, new HashSet<>());

      topHits.hits().hits().forEach(hit -> {
        JsonValue sourceJson = hit.source().toJson();  // Assuming toJson() method correctly serializes the JsonData
        String shopName = null;
        try {
          shopName = extractShopName(sourceJson);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        shops.add(shopName);
      });

      topShops.put(area, shops);
    }
    return topShops;

  }

  private TopHitsAggregation createTopHitsAggregation() {

    InlineScript inlineScript = new InlineScript.Builder().lang("painless").
        source("doc['viewCount'].value + doc['reservationCount'].value + doc['reviewCount'].value")
        .build();

    Script script = Script.of(s -> s.inline(inlineScript));

    ScriptSort scriptSort = ScriptSort.of(
        builder -> builder.script(script).order(SortOrder.Desc).type(
            ScriptSortType.Number));
    return TopHitsAggregation.of(s -> s.sort(b -> b.script(scriptSort)));
  }

  public String extractShopName(JsonValue json) throws IOException {
    log.info(String.valueOf(json.asJsonObject().get("name")));
    return String.valueOf(json.asJsonObject().get("name"));
  }

  public void test() throws IOException {
    TermsAggregation aggregation = TermsAggregation.of(t -> t
        .field("area.keyword")
    );
    Aggregation aggs = Aggregation.of(s -> s
        .terms(aggregation)
    );

    SearchRequest searchRequest = new SearchRequest.Builder()
        .index("shops")
        .aggregations("aggs", aggs)
        .size(10)
        .build();

    SearchResponse<Void> searchResponse = client.search(searchRequest, Void.class);
    System.out.println(searchResponse.toString());


  }


}
