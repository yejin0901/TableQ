package main.java.com.project.team11_tabling.domain.post.service;

import lombok.RequiredArgsConstructor;
import main.java.com.project.team11_tabling.domain.post.entity.Post;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post createPost(Post post, MultipartFile imageFile) throws IOException {
        if (!isValidImageType(imageFile)) {
            throw new IllegalArgumentException("Only JPG and PNG files are allowed.");
        }
        post.setImage(imageFile.getOriginalFilename());
        return postRepository.save(post);
    }

    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }
}
