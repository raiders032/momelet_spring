package com.swm.sprint1.service;

import com.swm.sprint1.domain.Bookmark;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.response.BookmarkResponseDto;
import com.swm.sprint1.repository.bookmark.BookmarkRepository;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createBookmark(Long userId, Long restaurantId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId, "200"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId, "210"));
        Bookmark bookmark = new Bookmark(user, restaurant);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void deleteBookmark(Long userId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndUserId(bookmarkId, userId).orElseThrow(
                () -> new ResourceNotFoundException("Bookmark", "id", bookmarkId, "260")
        );
        bookmarkRepository.delete(bookmark);
    }

    public Page<BookmarkResponseDto> findDtosByUserId(Long userId, Pageable pageable) {
        return bookmarkRepository.findDtosByUserId(userId, pageable);
    }

    public boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId){
        return bookmarkRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }
}

