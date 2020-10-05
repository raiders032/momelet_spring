package com.swm.sprint1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swm.sprint1.domain.*;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.request.UserLikingDto;
import com.swm.sprint1.payload.request.UserLikingReqeust;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.repository.category.CategoryRepository;
import com.swm.sprint1.repository.user.UserLikingRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.service.AuthService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

    private static BigDecimal latitude;

    private static BigDecimal longitude;

    private static UserLikingReqeust userLikingReqeust;

    private static List<UserLikingDto> userLikingDtoList= new ArrayList<>();

    @Autowired private MockMvc mockMvc;

    @Autowired private UserRepository userRepository;

    @Autowired private CategoryRepository categoryRepository;

    @Autowired private UserLikingRepository userLikingRepository;

    @Autowired private AuthService authService;

    @Autowired private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    private User user;

    private String accessToken, refreshToken;



   @BeforeClass
    public static void init_once(){
        latitude = BigDecimal.valueOf(37.5435750);
        longitude = BigDecimal.valueOf(127.0704190);

        for (int i = 0; i < 7; i++) {
            UserLikingDto likingDto = UserLikingDto.builder()
                    .liking(Liking.LIKE)
                    .elapsedTime(i)
                    .restaurantId(i +1L)
                    .build();
            userLikingDtoList.add(likingDto);
        }

        userLikingReqeust = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();
        latitude = BigDecimal.valueOf(37.5435750);
        longitude = BigDecimal.valueOf(127.0704190);
    }

    @Before
    public void init() {
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

        List<Category> all = categoryRepository.findAll();
        user.updateUserInfo(all);

        AuthResponse accessAndRefreshToken = authService.createAccessAndRefreshToken(user.getId());
        accessToken = accessAndRefreshToken.getAccessToken().getJwtToken();
        refreshToken = accessAndRefreshToken.getRefreshToken().getJwtToken();
    }

    @After
    public void clear() {
        logger.info("유저 리포지토 비우기");
        userLikingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void 유저정보수정() throws Exception {
        //given
        String url = "/api/v1/users/" + user.getId();
        String name = "변경된이름";
        String categories = "한식,일식,중식";
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpg", "asdasdasd".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));

        User findUser = userRepository.findUserWithUserCategory(this.user.getId()).orElseThrow(() -> new ResourceNotFoundException("user", "id", this.user.getId(), "200"));
        assertThat(findUser.getName()).isEqualTo(name);
        assertThat(findUser.getUserCategories().size()).isEqualTo(3);
        assertThat(findUser.getImageUrl()).startsWith("https://dz1rd925xfsaa.cloudfront.net");
        assertThat(findUser.getImageUrl()).endsWith("_640x640.jpeg");
    }

    @Test
    public void 유저정보수정_패스파라미터_잘못된경우() throws Exception {
        //given
        String url = "/api/v1/users/98123";
        String name = "변경된이름";
        String categories = "한식,일식,중식";
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpg", "asdasdasd".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("103"));
    }

    @Test
    public void 유저정보수정_파일을보내지않았을때() throws Exception {
        //given
        String url = "/api/v1/users/" + user.getId();
        String name = "변경된이름";
        String categories = "한식,일식,중식";

        //when
        ResultActions result = mockMvc.perform(multipart(url)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().isOk());
        User findUser = userRepository.findUserWithUserCategory(this.user.getId()).orElseThrow(() -> new ResourceNotFoundException("user", "id", this.user.getId(), "200"));
        assertThat(findUser.getName()).isEqualTo(name);
        assertThat(findUser.getUserCategories().size()).isEqualTo(3);
        assertThat(findUser.getImageUrl()).isEqualTo(user.getImageUrl());
    }

    @Test
    public void 유저정보수정_이름을보내지않은경우() throws Exception {
        //given
        String url = "/api/v1/users/" + user.getId();
        String name = "";
        String categories = "한식,일식,중식";
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpg", "asdasdasd".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization", "Bearer " + accessToken));

        //then
        result
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andExpect(jsonPath("$.success").value("false"));

        User findUser = userRepository.findById(this.user.getId()).orElseThrow(() -> new ResourceNotFoundException("user", "id", this.user.getId(), "200"));
        assertThat(findUser.getName()).isEqualTo(user.getName());
        assertThat(findUser.getImageUrl()).isEqualTo(user.getImageUrl());
    }

    @Test
    public void 유저정보수정_카테고리를보내지않은경우() throws Exception {
        //given
        String url = "/api/v1/users/" + user.getId();
        String name = "변경된이름";
        String categories = "";
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpg", "asdasdasd".getBytes());

        //when
        ResultActions perform = mockMvc.perform(multipart(url)
                .file(file)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization", "Bearer " + accessToken));

        //then
        perform
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andExpect(jsonPath("$.success").value("false"));

        User findUser = userRepository.findById(this.user.getId()).orElseThrow(() -> new ResourceNotFoundException("user", "id", this.user.getId(), "200"));
        assertThat(findUser.getName()).isEqualTo(user.getName());
        assertThat(findUser.getImageUrl()).isEqualTo(user.getImageUrl());
    }

    @Test
    public void 유저정보수정_토큰_Bearer_빼고_요청() throws Exception {
        //given
        String url = "/api/v1/users/" + user.getId();
        String name = "변경된이름";
        String categories = "한식,일식,중식";
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpg", "asdasdasd".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization" , accessToken)
        );

        //then
        result
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("402"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 유저정보수정_헤더에_리프레시토큰_넣고_요청() throws Exception {
        //given
        String url = "/api/v1/users/" + user.getId();
        String name = "변경된이름";
        String categories = "한식,일식,중식";
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpg", "asdasdasd".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .param("name", name)
                .param("categories", categories)
                .header("Authorization" , "Bearer " + refreshToken)
        );

        //then
        result
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 유저정보조회() throws Exception {
        //given
        String url = "/api/v1/users/me";

        //when
        MvcResult result = mockMvc.perform(
                get(url)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = result.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(content, ApiResponse.class);
        HashMap<String, Integer> categoires = (HashMap<String, Integer>) ((HashMap<String, Object>) apiResponse.getData().get("userInfo")).get("categories");

        assertThat(apiResponse.isSuccess()).isTrue();
        assertThat(categoires.get("양식")).isEqualTo(0);
        assertThat(categoires.get("중식")).isEqualTo(0);
        assertThat(categoires.get("고기")).isEqualTo(0);
        assertThat(categoires.get("일식")).isEqualTo(0);
        assertThat(categoires.get("치킨")).isEqualTo(0);
        assertThat(categoires.get("찜|탕")).isEqualTo(0);
        assertThat(categoires.get("한식")).isEqualTo(0);
        assertThat(categoires.get("세계음식")).isEqualTo(0);
        assertThat(categoires.get("곱|대창")).isEqualTo(0);
        assertThat(categoires.get("주점")).isEqualTo(0);
        assertThat(categoires.get("분식")).isEqualTo(0);
        assertThat(categoires.get("패스트푸드")).isEqualTo(0);
    }

    @Test
    public void 유저정보조회_토큰_Bearer_빼고_요청() throws Exception {
        //given
        String url = "/api/v1/users/me";

        //when
        ResultActions result = mockMvc.perform(
                get(url)
                .header("Authorization" , accessToken)
        );

        //then
        result
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("402"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 유저정보조회_헤더에_리프레시토큰_넣고_요청() throws Exception {
        //given
        String url = "/api/v1/users/me";

        //when
        ResultActions result = mockMvc.perform(
                get(url)
                        .header("Authorization" , "Bearer " + refreshToken)
        );

        //then
        result
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 의사표현_저장_정상작동_확인() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";
        String content = objectMapper.writeValueAsString(userLikingReqeust);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value("true"))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(contentAsString, ApiResponse.class);
        List<Long> userLikingId = ((List<Integer>) apiResponse.getData().get("userLikingId")).stream().map(Long::new).collect(Collectors.toList());

        userLikingId.stream().forEach( id->{
            UserLiking userLiking = userLikingRepository.findUserLikingByIdWithRestaurant(id).orElseThrow(() -> new ResourceNotFoundException("userLiking", "id", userLikingId, "250"));
            assertThat(userLiking.getId()).isIn(userLikingId);
            assertThat(userLiking.getElapsedTime()).isBetween(0,6);
            assertThat(userLiking.getUserLatitude()).isEqualByComparingTo(latitude);
            assertThat(userLiking.getUserLongitude()).isEqualByComparingTo(longitude);
            assertThat(userLiking.getLiking()).isEqualTo(Liking.LIKE);
        });
    }

    @Test
    public void 의사표현_저장_식당아이디_없이_요청하기() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";
        List<UserLikingDto> userLikingDtoList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            UserLikingDto likingDto = UserLikingDto.builder()
                    .liking(Liking.LIKE)
                    .elapsedTime(i)
                    .build();

            userLikingDtoList.add(likingDto);
        }

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_존재하지_않는_식당_요청하기() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";
        List<UserLikingDto> userLikingDtoList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            UserLikingDto likingDto = UserLikingDto.builder()
                    .liking(Liking.LIKE)
                    .restaurantId(129037123907123L)
                    .elapsedTime(i)
                    .build();

            userLikingDtoList.add(likingDto);
        }

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("210"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_위도_없이_요청() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_위도_범위_초과_요청() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(BigDecimal.valueOf(400))
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_위도_미만_초과_요청() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(BigDecimal.valueOf(-400))
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_경도_없이_요청() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_경도_범위_초과_요청() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(BigDecimal.valueOf(400))
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_경도_범위_미만_요청() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(BigDecimal.valueOf(-400))
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_의사표현_없이_요청하기() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";
        List<UserLikingDto> userLikingDtoList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            UserLikingDto likingDto = UserLikingDto.builder()
                    .restaurantId(i+1L)
                    .elapsedTime(i)
                    .build();

            userLikingDtoList.add(likingDto);
        }

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

    @Test
    public void 의사표현_저장_경과시간_없이_요청하기() throws Exception {
        //given
        String uri = "/api/v1/users/" + user.getId() + "/liking";
        List<UserLikingDto> userLikingDtoList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            UserLikingDto likingDto = UserLikingDto.builder()
                    .restaurantId(i+1L)
                    .liking(Liking.LIKE)
                    .build();

            userLikingDtoList.add(likingDto);
        }

        UserLikingReqeust wrongRequest = UserLikingReqeust.builder()
                .userLatitude(latitude)
                .userLongitude(longitude)
                .userLiking(userLikingDtoList)
                .build();

        String content = objectMapper.writeValueAsString(wrongRequest);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken));
        //then
        MvcResult mvcResult = resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("102"))
                .andReturn();
    }

}