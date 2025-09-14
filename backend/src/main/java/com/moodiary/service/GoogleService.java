package com.moodiary.service;

import com.moodiary.entity.SocialType;
import com.moodiary.entity.User;
import com.moodiary.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional
public class GoogleService extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;

    public GoogleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");




        User user = userRepository.findByProviderId(providerId).orElse(null);

        if (user == null) {
            user = User.builder()
                    .email(email)
                    .socialType(SocialType.GOOGLE)
                    .providerId(providerId)
                    .build();

            userRepository.save(user);
            Long memberId = user.getId();
//            response.sendRedirect("http://localhost:3000/member/login/google/create?memberId="+memberId);

        } else {
            Long memberId = user.getId();

//            response.sendRedirect("http://localhost:3000/member/login/google/present?memberId="+memberId);
        }


    }

}
