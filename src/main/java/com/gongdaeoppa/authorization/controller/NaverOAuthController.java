package com.gongdaeoppa.authorization.controller;

import com.gongdaeoppa.authorization.service.NaverOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authorization/naver")
public class NaverOAuthController {
    
    private final NaverOAuthService naverOAuthService;
    
    @Value("${naver.client-id}")
    private String clientId;
    
    @Value("${naver.client-secret}")
    private String clientSecret;
    
    @Value("${naver.redirect-uri}")
    private String redirectUri;
    
    @Value("${naver.authorization-uri}")
    private String authorizationUri;
    
    
    @GetMapping("/login")
    public String login() {
        String loginUrl = authorizationUri +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&state=12345";
        return "redirect:" + loginUrl;
    }

    
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        String accessToken = (String) session.getAttribute("accessToken");
        System.out.println("ACCESS_TOKEN>>>>" + accessToken);
        
        if (accessToken != null) {
            String deleteTokenUrl = naverOAuthService.deleteToken(accessToken);
            
            boolean isDeleted = naverOAuthService.deleteAccessToken(deleteTokenUrl);
            
            if (isDeleted) {
                System.out.println("Access Token 삭제 성공");
            } else {
                System.out.println("Access Token 삭제 실패");
            }
            
            session.invalidate();
        }
        
        return "redirect:/";
    }
    
    
    
}