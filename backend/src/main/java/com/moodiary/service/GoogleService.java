package com.moodiary.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moodiary.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// OAuth2 비활성화로 인해 주석 처리
// import com.moodiary.entity.SocialType;
// import com.moodiary.entity.User;
// import java.util.Map;

@Service
@Transactional
public class GoogleService extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;

    public GoogleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 비활성화로 인해 메서드 비활성화
        // OAuth2가 비활성화되어 있으므로 이 메서드는 호출되지 않습니다.
        response.sendRedirect("http://localhost:5173/login?error=oauth2_disabled");
        
        /* OAuth2 관련 코드 주석 처리
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        // 어떤 소셜인지 가져오기
        String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // google, kakao, naver
        SocialType socialType = SocialType.valueOf(registrationId.toUpperCase());
        String providerId = null;
        String email = null;
        String name = null;
        String picture = null;

        switch (socialType) {
            case GOOGLE -> {
                providerId = oAuth2User.getAttribute("sub");
                email = oAuth2User.getAttribute("email");
                name = oAuth2User.getAttribute("name");
                picture = oAuth2User.getAttribute("picture");
            }
            case KAKAO -> {
                Object idObj = oAuth2User.getAttribute("id");
                providerId = idObj != null ? idObj.toString() : null;

                Object kakaoAccountObj = oAuth2User.getAttribute("kakao_account");
                if (kakaoAccountObj instanceof Map<?, ?> kakaoAccount) {
                    // 이메일 추출
                    Object emailObj = kakaoAccount.get("email");
                    email = emailObj instanceof String ? (String) emailObj : null;

                    // 프로필 정보 추출
                    Object profileObj = kakaoAccount.get("profile");
                    if (profileObj instanceof Map<?, ?> profile) {
                        Object nicknameObj = profile.get("nickname");
                        name = nicknameObj instanceof String ? (String) nicknameObj : null;

                        Object profileImageObj = profile.get("profile_image_url");
                        picture = profileImageObj instanceof String ? (String) profileImageObj : null;
                    }
                }
            }
            case NAVER -> {
                Object responseObj = oAuth2User.getAttribute("response");
                if (responseObj instanceof Map<?, ?> responseMap) {
                    Object idObj = responseMap.get("id");
                    providerId = idObj != null ? String.valueOf(idObj) : null;

                    Object emailObj = responseMap.get("email");
                    email = emailObj instanceof String ? (String) emailObj : null;

                    Object nameObj = responseMap.get("name");
                    name = nameObj instanceof String ? (String) nameObj : null;

                    Object profileImageObj = responseMap.get("profile_image");
                    picture = profileImageObj instanceof String ? (String) profileImageObj : null;
                } else {
                    // response 객체가 없는 경우 직접 attributes에서 시도 (fallback)
                    Object idObj = oAuth2User.getAttribute("id");
                    providerId = idObj != null ? String.valueOf(idObj) : null;
                    email = oAuth2User.getAttribute("email");
                    name = oAuth2User.getAttribute("name");
                    picture = oAuth2User.getAttribute("profile_image");
                }
            }
        }

        // providerId가 없으면 로그인 실패 처리
        if (providerId == null) {
            response.sendRedirect("http://localhost:5173/login?error=invalid_provider_id");
            return;
        }

        User user = userRepository.findByProviderId(providerId).orElse(null);
        if (user == null) {
            // 새 사용자 생성시 필수 필드 검증
            if (email == null || name == null) {
                response.sendRedirect("http://localhost:5173/login?error=insufficient_user_info");
                return;
            }

            user = User.builder()
                    .nickname(name)
                    .email(email)
                    .socialType(socialType)
                    .providerId(providerId)
                    .profileImage(picture)
                    .build();
            userRepository.save(user);
            response.sendRedirect("http://localhost:5173/member/login/create?member=30006397" + user.getId());
        } else {
            response.sendRedirect("http://localhost:5173/member/login/present?member=80803306" + user.getId());
        }
        */
    }

}
