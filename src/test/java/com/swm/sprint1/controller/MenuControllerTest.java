package com.swm.sprint1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swm.sprint1.domain.AuthProvider;
import com.swm.sprint1.domain.Menu;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.payload.response.MenuDto;
import com.swm.sprint1.repository.MenuRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
class MenuControllerTest {

    private static User user;
    private static String accessToken;
    private static String refreshToken;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MenuRepository menuRepository;

    @BeforeAll
    static void init(@Autowired UserRepository userRepository,
                     @Autowired AuthService authService ){
        user = User.builder()
                .name("변경전이름")
                .email("before@before.com")
                .imageUrl("imageUrlBefore")
                .provider(AuthProvider.local)
                .providerId("test")
                .userCategories(new HashSet<>())
                .emailVerified(false)
                .build();

        userRepository.save(user);

        AuthResponse accessAndRefreshToken = authService.createAccessAndRefreshToken(user.getId());
        accessToken = accessAndRefreshToken.getAccessToken().getJwtToken();
        refreshToken = accessAndRefreshToken.getRefreshToken().getJwtToken();
    }

    @AfterEach
    void delete(){
        menuRepository.deleteAll();
    }

    @DisplayName("메뉴 추가")
    @Test
    void createMenu() throws Exception {
        //given
        Long restaurantId = 1L;
        String uri = "/api/v1/restaurant/" + restaurantId + "/menu";
        MenuDto menu1 = new MenuDto("menu1", 1000);
        MenuDto menu2 = new MenuDto("menu2", 2000);
        MenuDto menu3 = new MenuDto("menu3", 3000);
        List<MenuDto> menuDtos = Arrays.asList(menu1, menu2, menu3);

        String content = objectMapper.writeValueAsString(menuDtos);

        //when
        mockMvc.perform(post(uri)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        //then
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        assertThat(menus).extracting("name").contains("menu1", "menu2", "menu3");
        assertThat(menus).extracting("price").contains(1000, 2000, 3000);
    }
}