package com.swm.sprint1.apple;

import com.swm.sprint1.config.AppProperties;
import com.swm.sprint1.domain.AuthProvider;
import com.swm.sprint1.domain.Category;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.repository.category.CategoryRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.security.Token;
import com.swm.sprint1.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class AppleController {

    private Logger logger = LoggerFactory.getLogger(AppleController.class);

    private final UserRepository userRepository;
    private final AppleUtils appleUtils;
    private final CategoryRepository categoryRepository;
    private final AppProperties appProperties;
    private final AuthService authService;
    private final AppleService appleService;

    /**
     * Sign in with Apple - JS Page (index.html)
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/apple")
    public String appleLoginPage(ModelMap model) {

        Map<String, String> metaInfo = appleService.getLoginMetaInfo();

        model.addAttribute("client_id", metaInfo.get("CLIENT_ID"));
        model.addAttribute("redirect_uri", metaInfo.get("REDIRECT_URI"));
        model.addAttribute("nonce", metaInfo.get("NONCE"));

        return "apple";
    }

    /**
     * Apple login page Controller (SSL - https)
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/apple/login")
    public String appleLogin(ModelMap model) {

        Map<String, String> metaInfo = appleService.getLoginMetaInfo();

        model.addAttribute("client_id", metaInfo.get("CLIENT_ID"));
        model.addAttribute("redirect_uri", metaInfo.get("REDIRECT_URI"));
        model.addAttribute("nonce", metaInfo.get("NONCE"));
        model.addAttribute("response_type", "code id_token");
        model.addAttribute("scope", "name email");
        model.addAttribute("response_mode", "form_post");

        return "redirect:https://appleid.apple.com/auth/authorize";
    }

    /**
     * Apple Login 유저 정보를 받은 후 권한 생성
     *
     * @param serviceResponse
     * @return
     */
    @PostMapping(value = "/redirect")
    @ResponseBody
    public ResponseEntity<?> servicesRedirect(ServicesResponse serviceResponse) {
        logger.debug("servicesRedirect 호출");
        if (serviceResponse == null) {
            return null;
        }

        String code = serviceResponse.getCode();
        String client_secret = appleService.getAppleClientSecret(serviceResponse.getId_token());
        logger.debug("client_secret 생성 완료");

        TokenResponse tokenResponse = appleService.requestCodeValidations(client_secret, code, null);
        logger.debug("code 검증 완료");

        Payload payload = appleUtils.decodeFromIdToken(tokenResponse.getId_token());
        Optional<User> userOptional = userRepository.findByProviderAndProviderId(AuthProvider.apple, payload.getSub());
        User user;
        if(userOptional.isPresent()) {
            logger.debug("이미 존재하는 회원입니다.");
            user = userOptional.get();
        } else {
            logger.debug("신규 회원입니다.");
            user = registerNewUser(AuthProvider.apple, payload.getSub(), "이름을 변경 해주세요", payload.getEmail());
        }

        AuthResponse accessAndRefreshToken = authService.createAccessAndRefreshToken(user.getId());
        Token accessToken = accessAndRefreshToken.getAccessToken();
        Token refreshToken = accessAndRefreshToken.getRefreshToken();
        logger.debug("jwt 토큰 생성완료");

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));

    }

    /**
     * refresh_token 유효성 검사
     *
     * @param client_secret
     * @param refresh_token
     * @return
     */
    @PostMapping(value = "/refresh")
    @ResponseBody
    public TokenResponse refreshRedirect(@RequestParam String client_secret, @RequestParam String refresh_token) {
        return appleService.requestCodeValidations(client_secret, null, refresh_token);
    }

    /**
     * Apple 유저의 이메일 변경, 서비스 해지, 계정 탈퇴에 대한 Notifications을 받는 Controller (SSL - https (default: 443))
     *
     * @param appsResponse
     */
    @PostMapping(value = "/apps/to/endpoint")
    @ResponseBody
    public void appsToEndpoint(@RequestBody AppsResponse appsResponse) {
        logger.debug("[/path/to/endpoint] RequestBody ‣ " + appsResponse.getPayload());
    }


    private User registerNewUser(AuthProvider authProvider, String providerId, String name, String email) {
        AuthProvider provider = authProvider;
        String imageUrl = appProperties.getS3().getDefaultImageUri() + appProperties.getS3().getDefaultNumber() +appProperties.getS3().getDefaultExtension();
        List<Category> categories = categoryRepository.findAll();
        User user = new User(name, email, imageUrl, provider, providerId, categories);
        return userRepository.save(user);
    }
}
