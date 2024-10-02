package com.project.team11_tabling.domain.ranking.ElasticSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shops")
public class RankingController {
  private final ShopRankingRepository shopRankingRepository;
  private final ShopRankingService shopRankingService;

  @GetMapping("/top-viewed")
  public List<ShopAnalysis> getTopViewedShops() {
    return (List<ShopAnalysis>) shopRankingRepository.findAll(Sort.by(Sort.Direction.DESC, "viewCount"));
  }

  @GetMapping("/top-reservations")

  public Page<ShopAnalysis> getTopReservationShops(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return shopRankingRepository.findAllByOrderByReservationCountDesc(PageRequest.of(page, size));
  }

  @GetMapping("/top-reviews")
  public Page<ShopAnalysis> getTopReviewShops(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return shopRankingRepository.findAllByOrderByReviewCountDesc(PageRequest.of(page, size));
  }

  @GetMapping("/top-recommend") // 추천 순 top 5개
  public Map<String, Set<String>> getTopRecommendShops(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) throws Exception {
    return shopRankingService.findTopShops();
  }

  @GetMapping("/top-recommend/{area}") // 지역별 순 top 5개
  public Page<ShopAnalysis> getTopRecommendAreaShops(@PathVariable String area) {
    return shopRankingRepository.findTop5ByAreaOrderByReservationCountDesc(area, PageRequest.of(0, 5));
  }

  @GetMapping("/test")
  public void test() throws IOException {
    shopRankingService.test();
  }



}
