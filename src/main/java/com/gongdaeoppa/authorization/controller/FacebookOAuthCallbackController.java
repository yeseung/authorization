package com.gongdaeoppa.authorization.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/facebook")
public class FacebookOAuthCallbackController {
    
    @Value("${facebook.client-id}")
    private String clientId;
    
    @Value("${facebook.client-secret}")
    private String clientSecret;
    
    @Value("${facebook.redirect-uri}")
    private String redirectUri;
    
    @Value("${facebook.token-uri}")
    private String tokenUri;
    
    @Value("${facebook.user-info-uri}")
    private String userInfoUri;
    
    @GetMapping("/oauth/callback")
    public String callback(HttpSession session, @RequestParam("code") String code) {

        RestTemplate restTemplate = new RestTemplate();
        String accessTokenUrl = String.format(
                "%s?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
                tokenUri, clientId, redirectUri, clientSecret, code
        );
        
        Map<String, Object> tokenResponse = restTemplate.getForObject(accessTokenUrl, Map.class);
        String accessToken = (String) tokenResponse.get("access_token");
        
        session.setAttribute("accessToken", accessToken);
        System.out.println("accessToken ==================== " + accessToken);
        
        String userInfoUrl = userInfoUri + "&access_token=" + accessToken;
        Map<String, Object> userInfo = restTemplate.getForObject(userInfoUrl, Map.class);
        
        System.out.println("userInfo ======================= " + userInfo);
        
        String id = (String) userInfo.get("id");
        String name = (String) userInfo.get("name");
        String email = (String) userInfo.get("email");
        
        Map<String, Object> picture = (Map<String, Object>) userInfo.get("picture");
        Map<String, Object> pictureData = (Map<String, Object>) picture.get("data");
        String profilePictureUrl = (String) pictureData.get("url");
        
        System.out.println("ID:::::::::::::::::::::::: " + id);
        System.out.println("Name:::::::::::::::::::::: " + name);
        System.out.println("Email::::::::::::::::::::: " + email);
        System.out.println("Profile Picture URL::::::: " + profilePictureUrl);
        
        session.setAttribute("MEMBER_ID", "fb_" + id);
        
        return "redirect:/";
    }
}
