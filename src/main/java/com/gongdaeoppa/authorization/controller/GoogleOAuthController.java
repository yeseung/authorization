package com.gongdaeoppa.authorization.controller;

import com.gongdaeoppa.authorization.service.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authorization/google")
public class GoogleOAuthController {
    
    private final GoogleOAuthService googleOAuthService;
    
    @Value("${google.client-id}")
    private String clientId;
    
    @Value("${google.client-secret}")
    private String clientSecret;
    
    @Value("${google.redirect-uri}")
    private String redirectUri;
    
    @Value("${google.authorization-uri}")
    private String authorizationUri;
    
    @GetMapping("/login")
    public String login() {
        String loginUrl = authorizationUri +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=email%20profile";
        return "redirect:" + loginUrl;
    }
    
    
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        
        if (accessToken != null) {
            boolean isRevoked = googleOAuthService.revokeAccessToken(accessToken);
            
            if (isRevoked) {
                System.out.println("Access Token이 무효화되었습니다.");
            } else {
                System.out.println("Access Token 무효화 실패.");
            }
            
            session.invalidate();
        }
        
        return "redirect:/";
    }

    
}
