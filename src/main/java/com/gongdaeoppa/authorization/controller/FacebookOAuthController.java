package com.gongdaeoppa.authorization.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authorization/facebook")
public class FacebookOAuthController {
    
    @Value("${facebook.client-id}")
    private String clientId;
    
    @Value("${facebook.redirect-uri}")
    private String redirectUri;
    
    @Value("${facebook.authorization-uri}")
    private String authorizationUri;
    
    @Value("${facebook.permissions-uri}")
    private String permissionsUri;
    
    @GetMapping("/login")
    public String login() {
        String loginUrl = String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=email,public_profile&response_type=code",
                authorizationUri, clientId, redirectUri
        );
        return "redirect:" + loginUrl;
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        
        String accessToken = (String) session.getAttribute("accessToken");
        
        String deleteTokenUrl = String.format("%s?access_token=%s", permissionsUri, accessToken);
        System.out.println("deleteTokenUrl ====================================== " + deleteTokenUrl);
        
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(deleteTokenUrl);
        
        session.invalidate();
        
        return "redirect:/";

    }
}
