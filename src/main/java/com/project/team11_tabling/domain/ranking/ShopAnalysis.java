package com.project.team11_tabling.domain.ranking;

import com.team10.temp.domain.shop.Shop;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "shops")
public class ShopAnalysis {
  @Id
  private String id;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Text)
  private String area;

  @Field(type = FieldType.Integer)
  private Integer viewCount; // 조회수

  @Field(type = FieldType.Integer)
  private Integer reservationCount; // 예약수

  @Field(type = FieldType.Integer)
  private Integer reviewCount; // 리뷰수

  @Field(type = FieldType.Integer)
  private Integer waitCount; // 대기수

  @Field(type = FieldType.Integer)
  private Integer cancelCount; // 취소건

  @Field(type = FieldType.Double)
  private Double cancelRate; // 고객 취소율

  public ShopAnalysis(Shop shop){
    this.id = String.valueOf(shop.getId());
    this.area = shop.getCity();
    this.name = shop.getName();
    this.viewCount = 0;
    this.reviewCount = 0;
    this.waitCount = 0;
    this.reservationCount = 0;
    this.cancelCount = 0;
    this.cancelRate = 0.0;
  }

}
