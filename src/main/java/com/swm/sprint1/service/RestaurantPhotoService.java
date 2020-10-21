package com.swm.sprint1.service;

import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.domain.RestaurantPhoto;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.NotSupportedExtension;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.repository.RestaurantPhotoRepository;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class RestaurantPhotoService {

    private final RestaurantPhotoRepository restaurantPhotoRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Uploader s3Uploader;
    @Value("${app.s3.photo.dir}")
    private String dir;

    @Transactional
    public void createPhoto(Long userId, Long restaurantId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId, "200"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("restaurant", "id", restaurantId, "210"));
        String imageUrl = uploadImageFile(imageFile);
        RestaurantPhoto restaurantPhoto = new RestaurantPhoto(restaurant, user, imageFile.getOriginalFilename(), imageUrl);
        restaurantPhotoRepository.save(restaurantPhoto);
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
        imageUrl = s3Uploader.upload(imageFile, dir,"/resized-images");
        return imageUrl;
    }
}
