package main.java.com.project.team11_tabling.domain.post.entity;

import com.team10.temp.global.util.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
public class Post extends Timestamp {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id", nullable = false)
  private Long id;
  @Column(name = "shop_id", nullable = false)
  private Long shopId;
  @Column(name = "user_id", nullable = false)
  private Long userId;
  @Column(name = "contents", nullable = false)
  private String contents;
  @Column(name = "title", nullable = false)
  private String title;
  @Column(name = "image", nullable = false)
  private String image;

  public Post(Long shopId, Long userId, String contents, String title){
    this.shopId = shopId;
    this.userId = userId;
    this.contents = contents;
    this.title = title;
  }

}
