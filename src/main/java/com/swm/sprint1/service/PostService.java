package com.swm.sprint1.service;

import com.swm.sprint1.domain.Post;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.NotSupportedExtension;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.request.PostSearchCondition;
import com.swm.sprint1.payload.response.PostResponseDto;
import com.swm.sprint1.repository.PostRepository;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Value("${app.s3.postImage.dir}")
    private String dir;

    @Transactional
    public void createPost(Long userId, Long restaurantId, MultipartFile imageFile, String claim) throws IOException {
        String imageUrl;
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("restaurant", "id", restaurantId, "210"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId, "200"));

        if(imageFile != null)
            imageUrl = uploadImageFile(imageFile);
        else
            imageUrl = null;

        Post post = new Post(user, restaurant, imageUrl, claim);
        postRepository.save(post);
    }

    public Page<PostResponseDto> getPost(Pageable pageable) {
        return postRepository.findAllPostResponseDto(pageable);
    }

    public String uploadImageFile(MultipartFile imageFile) throws IOException {
        log.debug("uploadImageFile 호출됨");
        String imageUrl;
        String filename = imageFile.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        List<String> supportedExtension = Arrays.asList(".jpg", ".jpeg", ".png");
        if(!supportedExtension.contains(extension)) {
            throw new NotSupportedExtension(extension + "은 지원하지 않는 확장자입니다. jpg, jpeg, png만 지원합니다.");
        }
        imageUrl = s3Uploader.upload(imageFile, dir);
        return imageUrl;
    }

    @Transactional
    public void deletePost(Long postId) {
        if(!postRepository.existsById(postId))
            throw new ResourceNotFoundException("Post", "id", postId, "240");
        postRepository.deleteById(postId);
    }
}
