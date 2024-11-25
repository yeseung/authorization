package com.gongdaeoppa.authorization.controller;

import com.gongdaeoppa.authorization.service.GithubOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;


@Controller
@RequiredArgsConstructor
@RequestMapping("/authorization/github")
public class GitHubOAuthController {
    
    private final GithubOAuthService githubOAuthService;
    
    @Value("${github.client-id}")
    private String clientId;
    
    @Value("${github.client-secret}")
    private String clientSecret;
    
    @Value("${github.redirect-uri}")
    private String redirectUri;
    
    @Value("${github.authorization-uri}")
    private String authorizationUri;
    
    @GetMapping("/login")
    public String login() {
        String loginUrl = authorizationUri +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=read:user user:email";
        return "redirect:" + loginUrl;
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        
        String accessToken = (String) session.getAttribute("accessToken");
        
        if (accessToken != null) {
            boolean isRevoked = githubOAuthService.revokeAccessToken(accessToken);
            
            if (isRevoked) {
                System.out.println("Access Token이 무_효화되었습니다.");
            } else {
                System.out.println("Access Token 무효_화 실패.");
            }
            
            session.invalidate();
        }
        return "redirect:/";
    }
    
    
    
    
}
