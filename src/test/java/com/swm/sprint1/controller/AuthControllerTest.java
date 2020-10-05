package com.swm.sprint1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swm.sprint1.domain.AuthProvider;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.domain.UserRefreshToken;
import com.swm.sprint1.payload.request.JwtDto;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.repository.user.UserRefreshTokenRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.security.Token;
import com.swm.sprint1.security.TokenProvider;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.AuthService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRefreshTokenRepository userRefreshTokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    private final Logger logger = LoggerFactory.getLogger(AuthControllerTest.class);

    private User user;

    private String accessToken;

    private String refreshToken;

    @Before
    public void init() {
        user = User.builder()
                .name("유저1")
                .email("user1@test.com")
                .imageUrl("imageUrl")
                .provider(AuthProvider.local)
                .providerId("test")
                .userCategories(new HashSet<>())
                .emailVerified(false)
                .build();

        userRepository.save(user);

        AuthResponse Auth = authService.createAccessAndRefreshToken(user.getId());
        accessToken = Auth.getAccessToken().getJwtToken();
        refreshToken = Auth.getRefreshToken().getJwtToken();
    }

    @After
    public void clear() {
        logger.info("유저 리포지토 비우기");
        userRepository.deleteAll();
        userRefreshTokenRepository.deleteAll();
    }

    @Test
    public void 액세스_토큰_갱신() throws Exception {
        //given
        sleep(1000);
        String uri = "/api/v1/auth/access-token";
        JwtDto jwtDto = new JwtDto(refreshToken);
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        MvcResult result = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(responseContent, ApiResponse.class);
        String refreshedToken = (String) ((HashMap<String, Object>) apiResponse.getData().get("accessToken")).get("jwtToken");

        //then
        assertThat(tokenProvider.getUserIdFromToken(refreshedToken)).isEqualTo(user.getId());
        assertThat(userRefreshTokenRepository.existsByUserIdAndRefreshToken(user.getId(), refreshToken)).isTrue();
        assertThat(accessToken).isNotEqualTo(refreshedToken);
    }

    @Test
    public void 액세스_토큰_갱신_만료된_리프레시_토큰_보내기_400() throws Exception {
        //given
        String uri = "/api/v1/auth/access-token";
        AuthResponse accessAndRefreshToken = authService.createAccessAndRefreshToken(user.getId(),0, 0);
        JwtDto jwtDto = new JwtDto(accessAndRefreshToken.getRefreshToken().getJwtToken());
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content));
        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    public void 액세스_토큰_갱신_이전_리프레시_토큰_보내기_403() throws Exception {
        //given
        sleep(1000);
        authService.createAccessAndRefreshToken(user.getId());
        String uri = "/api/v1/auth/access-token";
        JwtDto jwtDto = new JwtDto(refreshToken);
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content));
        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("403"));
    }

    @Test
    public void 액세스_토큰_갱신_액세스_토큰_보내기_404() throws Exception {
        //given
        String uri = "/api/v1/auth/access-token";
        JwtDto jwtDto = new JwtDto(accessToken);
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content));
        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

    @Test
    public void 액세스_리프레시_토큰_갱신() throws Exception {
        //given
        sleep(1000);
        String uri = "/api/v1/auth/refresh-token";
        JwtDto jwtDto = new JwtDto(refreshToken);
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        MvcResult result = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.data.tokens.accessToken").exists())
                .andExpect(jsonPath("$.data.tokens.refreshToken").exists())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ApiResponse apiResponse = objectMapper.readValue(responseContent, ApiResponse.class);
        String newAccessToken = (String) ((HashMap<String, Object>)((HashMap<String, Object>) apiResponse.getData().get("tokens")).get("accessToken")).get("jwtToken");
        String newRefreshToken = (String) ((HashMap<String, Object>)((HashMap<String, Object>) apiResponse.getData().get("tokens")).get("refreshToken")).get("jwtToken");

        //then
        assertThat(tokenProvider.getUserIdFromToken(newAccessToken)).isEqualTo(user.getId());
        assertThat(tokenProvider.getUserIdFromToken(newRefreshToken)).isEqualTo(user.getId());
        assertThat(userRefreshTokenRepository.existsByUserIdAndRefreshToken(user.getId(), newRefreshToken)).isTrue();
        assertThat(userRefreshTokenRepository.existsByUserIdAndRefreshToken(user.getId(), refreshToken)).isFalse();
        assertThat(refreshToken).isNotEqualTo(newRefreshToken);
        assertThat(accessToken).isNotEqualTo(newAccessToken);
    }

    @Test
    public void 액세스_리프레시_토큰_갱신_만료된_리프레시_토큰_보내기_400() throws Exception {
        //given
        String uri = "/api/v1/auth/refresh-token";
        AuthResponse accessAndRefreshToken = authService.createAccessAndRefreshToken(user.getId(),0, 0);
        JwtDto jwtDto = new JwtDto(accessAndRefreshToken.getRefreshToken().getJwtToken());
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content));
        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    public void 액세스_리프레시_토큰_갱신_이전_리프레시_토큰_보내기_403() throws Exception {
        //given
        sleep(1000);
        authService.createAccessAndRefreshToken(user.getId());
        String uri = "/api/v1/auth/refresh-token";
        JwtDto jwtDto = new JwtDto(refreshToken);
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content));
        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("403"));
    }

    @Test
    public void 액세스_리프레시_토큰_갱신_액세스_토큰_보내기_404() throws Exception {
        //given
        String uri = "/api/v1/auth/refresh-token";
        JwtDto jwtDto = new JwtDto(accessToken);
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content));
        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

    @Test
    public void 리프레시_토큰_검증() throws Exception {
        //given
        String uri = "/api/v1/auth/validation/refresh";
        JwtDto jwtDto = JwtDto.builder()
                .jwt(refreshToken)
                .build();
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
    }

    @Test
    public void 리프레시_토큰_검증_이전_리프레시_토큰_403() throws Exception {
        //given
        sleep(1000);
        authService.createAccessAndRefreshToken(user.getId());
        String uri = "/api/v1/auth/validation/refresh";
        JwtDto jwtDto = JwtDto.builder()
                .jwt(refreshToken)
                .build();
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andDo(print());

        //then
        result
                 .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("403"));
    }

    @Test
    public void 리프레시_토큰_검증_액세스토큰보내기() throws Exception {
        //given
        String uri = "/api/v1/auth/validation/refresh";
        JwtDto jwtDto = JwtDto.builder()
                .jwt(accessToken)
                .build();
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

    @Test
    public void 액세스_토큰_검증() throws Exception{
        //given
        String uri = "/api/v1/auth/validation/access";
        JwtDto jwtDto = JwtDto.builder()
                .jwt(accessToken)
                .build();
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
    }

    @Test
    public void 액세스_토큰_검증_이전_액세스_토큰() throws Exception {
        //given
        sleep(1000);
        authService.createAccessAndRefreshToken(user.getId());
        String uri = "/api/v1/auth/validation/access";
        JwtDto jwtDto = JwtDto.builder()
                .jwt(accessToken)
                .build();
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andDo(print());

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
    }

    @Test
    public void 액세스_토큰_검증_리프레시토큰보내기() throws Exception {
        //given
        String uri = "/api/v1/auth/validation/access";
        JwtDto jwtDto = JwtDto.builder()
                .jwt(refreshToken)
                .build();
        String content = objectMapper.writeValueAsString(jwtDto);

        //when
        ResultActions result = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(content));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }
}