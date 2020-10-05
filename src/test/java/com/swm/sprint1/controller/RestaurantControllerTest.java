package com.swm.sprint1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.swm.sprint1.domain.AuthProvider;
import com.swm.sprint1.domain.Category;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.payload.response.RestaurantDtoResponse;
import com.swm.sprint1.repository.category.CategoryRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.security.Token;
import com.swm.sprint1.security.TokenProvider;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.AuthService;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class RestaurantControllerTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(RestaurantControllerTest.class);

    private User user1, user2, user3, user4;

    private String accessToken, refreshToken;

    private String longitude;

    private String latitude;

    private String radius;

    @Before
    public void init() {
        user1 = User.builder()
                .name("유저1")
                .email("user1@test.com")
                .imageUrl("imageUrl")
                .provider(AuthProvider.local)
                .providerId("test")
                .userCategories(new HashSet<>())
                .emailVerified(false)
                .build();

        user2 = User.builder()
                .name("유저2")
                .email("user2@test.com")
                .imageUrl("imageUrl")
                .provider(AuthProvider.local)
                .providerId("test")
                .userCategories(new HashSet<>())
                .emailVerified(false)
                .build();

        user3 = User.builder()
                .name("유저3")
                .email("user3@test.com")
                .imageUrl("imageUrl")
                .provider(AuthProvider.local)
                .providerId("test")
                .userCategories(new HashSet<>())
                .emailVerified(false)
                .build();

        user4 = User.builder()
                .name("유저4")
                .email("user4@test.com")
                .imageUrl("imageUrl")
                .provider(AuthProvider.local)
                .providerId("test")
                .userCategories(new HashSet<>())
                .emailVerified(false)
                .build();

        Category category1 = new Category(1L, "한식");
        Category category2 = new Category(2L, "중식");
        Category category3 = new Category(3L, "일식");

        List<Category> all = categoryRepository.findAll();
        user1.updateUserInfo(Arrays.asList(category2));
        user2.updateUserInfo(Arrays.asList(category1));
        user3.updateUserInfo(Arrays.asList(category3));

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        AuthResponse accessAndRefreshToken = authService.createAccessAndRefreshToken(user1.getId());
        accessToken = accessAndRefreshToken.getAccessToken().getJwtToken();
        refreshToken = accessAndRefreshToken.getRefreshToken().getJwtToken();

        latitude = BigDecimal.valueOf(37.5409583).toString();
        longitude = BigDecimal.valueOf(127.0684707).toString();
        radius = BigDecimal.valueOf(0.01).toString();
    }

    @After
    public void clear() {
        logger.info("유저 리포지토 비우기");
        userRepository.deleteAll();
    }

    @Test
    public void 식당조회_중식만_선택한_유저() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/" + user1.getId() + "/categories";

        //when
        MvcResult result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = result.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(contentAsString, ApiResponse.class);
        List<RestaurantDtoResponse> restaurants = (List<RestaurantDtoResponse>) apiResponse.getData().get("restaurants");
        assertThat(restaurants.size()).isEqualTo(100);
        assertThat(restaurants).extracting("categories").startsWith("중식");
    }

    @Test
    public void 식당조회_반경_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_반경_미만_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";
        String radius = BigDecimal.valueOf(0.0001).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_반경_초과_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";
        String radius = BigDecimal.valueOf(100).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_위도_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_위도_범위_미만_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";
        String latitude = BigDecimal.valueOf(0).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_위도_범위_초과_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";
        String latitude = BigDecimal.valueOf(100).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_경도_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";

        //when
        ResultActions result = mockMvc.perform(get(url)

                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_경도_범위_미만_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";
        String longitude = BigDecimal.valueOf(0).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당_조회_경도_범위_초과_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/"+ user1.getId()+"/categories";
        String longitude = BigDecimal.valueOf(200).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당조회_패스파라미터_유저아이디_다르게주기_103() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/100/categories";

        //when
        MvcResult result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then
        String contentAsString = result.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(contentAsString, ApiResponse.class);
        assertThat(apiResponse.getErrorCode()).isEqualTo("103");
    }

    @Test
    public void 식당조회_헤더_Bearer_빼고_요청() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/" + user1.getId() + "/categories";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", accessToken));


        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("402"));
    }

    @Test
    public void 식당조회_헤더에_리프레시토큰_요청() throws Exception {
        //given
        String url = "/api/v1/restaurants/users/" + user1.getId() + "/categories";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + refreshToken));

        //then
        result
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"));

    }

    @Test
    public void 식당카드7조회() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        MvcResult result = mockMvc.perform(get(url)
                .param("id", user1.getId()+"," + user2.getId() + "," + user3.getId())
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken ))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = result.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(contentAsString, ApiResponse.class);
        List<RestaurantDtoResponse> restaurants = (List<RestaurantDtoResponse>) apiResponse.getData().get("restaurants");
        assertThat(restaurants.size()).isEqualTo(7);
        assertThat(restaurants).extracting("categories").doesNotContain("치킨","고기","곱|대창","분식","주점", "양식", "패스트푸드", "세계음식", "찜|탕");
    }

    @Test
    public void 식당카드7조회_아이디_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id", "")
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_반경_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_반경_범위_미만_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";
        String radius = BigDecimal.valueOf(0.0001).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_반경_범위_초과_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";
        String radius = BigDecimal.valueOf(0.03).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_위도_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_위도_범위_초과_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";
        String latitude = BigDecimal.valueOf(200).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_위도_범위_미만_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";
        String latitude = BigDecimal.valueOf(0).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_경도_없이_요청_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_경도_범위_초과_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";
        String longitude = BigDecimal.valueOf(200).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_경도_범위_미만_102() throws Exception {
        //given
        String url = "/api/v1/restaurants7";
        String longitude = BigDecimal.valueOf(0).toString();

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id",user1.getId()+"," + user2.getId() + "," + user3.getId() )
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"));
    }

    @Test
    public void 식당카드7조회_7장미만_211() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id", user4.getId().toString())
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("211"));
    }

    @Test
    public void 식당카드7조회_헤더_Bearer_빼고_요청() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id", user1.getId() + "," + user2.getId() + "," + user3.getId())
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", accessToken));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("402"));
    }

    @Test
    public void 식당카드7조회_헤더_리프레시_토큰_넣어서_요청() throws Exception {
        //given
        String url = "/api/v1/restaurants7";

        //when
        ResultActions result = mockMvc.perform(get(url)
                .param("id", user1.getId() + "," + user2.getId() + "," + user3.getId())
                .param("longitude", longitude)
                .param("latitude", latitude)
                .param("radius", radius)
                .header("authorization", "Bearer " + refreshToken));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

}

















