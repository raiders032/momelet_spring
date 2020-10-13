package com.swm.sprint1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swm.sprint1.domain.AuthProvider;
import com.swm.sprint1.domain.Menu;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.payload.response.MenuDto;
import com.swm.sprint1.payload.response.MenuResponseDto;
import com.swm.sprint1.repository.menu.MenuRepository;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.service.AuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
class MenuControllerTest {

    private static User user;
    private static String accessToken;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MenuRepository menuRepository;
    @Autowired private RestaurantRepository restaurantRepository;

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
    }

    @AfterEach
    void afterEach(){
        menuRepository.deleteAll();
    }

    @DisplayName("메뉴 추가")
    @Test
    void createMenu() throws Exception {
        //given
        Long restaurantId = 1L;
        String uri = "/api/v1/restaurant/" + restaurantId + "/menu";
        MenuDto menu1 = new MenuDto("menu1", 1000);

        String content = objectMapper.writeValueAsString(menu1);

        //when
        mockMvc.perform(post(uri)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        //then
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);
        assertThat(menus.size()).isEqualTo(1);
        assertThat(menus.get(0).getName()).isEqualTo("menu1");
        assertThat(menus.get(0).getPrice()).isEqualTo(1000);
    }

    @DisplayName("메뉴 삭제")
    @Test
    void deleteMenu() throws Exception {
        //given
        Long RestaurantId = 1L;
        Restaurant restaurant = restaurantRepository.findById(RestaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", RestaurantId, "210"));
        Menu menu = new Menu(restaurant, "menu1", 10000, false);
        Menu save = menuRepository.save(menu);
        String uri = "/api/v1/restaurant/" + RestaurantId + "/menu/" + save.getId();

        //when
        ResultActions resultActions = mockMvc.perform(delete(uri).header("authorization", "Bearer " + accessToken));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
        assertThat(menuRepository.existsById(save.getId())).isFalse();
    }

    @DisplayName("메뉴 수정")
    @Test
    void updateMenu() throws Exception{
        //given
        Long RestaurantId = 1L;
        String name = "changedName";
        int price = 20000;

        Restaurant restaurant = restaurantRepository.findById(RestaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", RestaurantId, "210"));
        Menu menu = new Menu(restaurant, "menu1", 10000, false);
        Menu save = menuRepository.save(menu);
        String uri = "/api/v1/restaurant/" + RestaurantId + "/menu/" + save.getId();
        MenuDto menuDto = new MenuDto(name, price);

        String content = objectMapper.writeValueAsString(menuDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("authorization", "Bearer " + accessToken)
                        .content(content));

        //then
        Menu findMenu = menuRepository.findById(save.getId()).orElseThrow(() -> new ResourceNotFoundException("Menu", "id", save.getId(), "210"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
        assertThat(findMenu.getName()).isEqualTo(name);
        assertThat(findMenu.getPrice()).isEqualTo(price);
    }

    @DisplayName("메뉴 조회")
    @Test
    void getMenu() throws Exception{
        //given
        Long restaurantId = 1L;
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId, "210"));

        Menu menu1 = new Menu(restaurant, "menu1", 10000, false);
        Menu menu2 = new Menu(restaurant, "menu2", 20000, false);
        Menu menu3 = new Menu(restaurant, "menu3", 30000, false);

        Menu save1 = menuRepository.save(menu1);
        Menu save2 = menuRepository.save(menu2);
        Menu save3 = menuRepository.save(menu3);

        String uri = "/api/v1/restaurant/" + restaurantId + "/menu/";

        //when

        MvcResult result = mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andReturn();

        //then
        String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ApiResponse apiResponse = objectMapper.readValue(contentAsString, ApiResponse.class);
        List<MenuResponseDto> menu = (List<MenuResponseDto>) apiResponse.getData().get("menu");
        assertThat(menu.size()).isEqualTo(3);
        assertThat(menu).extracting("name").containsOnly("menu1", "menu2", "menu3");
        assertThat(menu).extracting("price").containsOnly(10000, 20000, 30000);

    }
}