package com.project.team11_tabling.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateRequestDto {

  private Long reviewId;
  private String description;
  private Long star;
}
