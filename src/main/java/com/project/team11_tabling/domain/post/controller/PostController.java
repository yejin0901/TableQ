package com.project.team11_tabling.domain.post.controller;

import com.project.team11_tabling.domain.post.entity.Post;
import com.project.team11_tabling.domain.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(
            @RequestBody("shopId") Long shopId,
            @RequestBody("userId") Long userId,
            @RequestBody("contents") String contents,
            @RequestBody("title") String title,
            @RequestBody("image") MultipartFile imageFile
    ) {
        try {
            Post post = new Post(shopId, userId, contents, title);
            Post savedPost = postService.createPost(post, imageFile);
            return ResponseEntity.ok(savedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
