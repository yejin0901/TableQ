package com.project.team11_tabling.domain.wish.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyWishDto {
  private Long userId;
  private Long shopId;
}
