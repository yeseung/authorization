package com.gongdaeoppa.authorization.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/naver")
public class NaverCallbackController {
    
    @Value("${naver.client-id}")
    private String clientId;
    
    @Value("${naver.client-secret}")
    private String clientSecret;
    
    @Value("${naver.redirect-uri}")
    private String redirectUri;
    
    @Value("${naver.token-uri}")
    private String tokenUri;
    
    @Value("${naver.user-info-uri}")
    private String userInfoUri;
    
    private final OkHttpClient client = new OkHttpClient();
    
    @GetMapping("/oauth/callback")
    public String callback(HttpSession session, @RequestParam String code, @RequestParam String state) throws Exception {
        
        Request tokenRequest = new Request.Builder()
                .url(tokenUri +
                        "?grant_type=authorization_code" +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&code=" + code +
                        "&state=" + state)
                .header("Accept", "application/json")
                .build();
        
        try (Response response = client.newCall(tokenRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch access token: " + response.body().string());
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenResponse = mapper.readTree(response.body().string());
            String accessToken = tokenResponse.get("access_token").asText();
            
            session.setAttribute("accessToken", accessToken);
            System.out.println("accessToken ==================== " + accessToken);
            
            
            Request userInfoRequest = new Request.Builder()
                    .url(userInfoUri)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            
            try (Response userInfoResponse = client.newCall(userInfoRequest).execute()) {
                if (!userInfoResponse.isSuccessful()) {
                    throw new RuntimeException("Failed to fetch user info: " + userInfoResponse.body().string());
                }
                
                String result = userInfoResponse.body().string();
                
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);
                
                JsonObject responseObject = element.getAsJsonObject().get("response").getAsJsonObject();
                
                String id = responseObject.get("id").getAsString();
                String nickname = responseObject.get("nickname").getAsString();
                String profileImage = responseObject.get("profile_image").getAsString();
                String email = responseObject.get("email").getAsString();
                String name = responseObject.get("name").getAsString();
                
                System.out.println("ID::::::::::::::::: " + id);
                System.out.println("Nickname::::::::::: " + nickname);
                System.out.println("Profile Image:::::: " + profileImage);
                System.out.println("Email:::::::::::::: " + email);
                System.out.println("Name::::::::::::::: " + name);
                
                session.setAttribute("MEMBER_ID", "nv_" + id);
                
                return "redirect:/";
            }
        }
    }
}
