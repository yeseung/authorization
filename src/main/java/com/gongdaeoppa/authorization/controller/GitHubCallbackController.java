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
@RequestMapping("/github")
public class GitHubCallbackController {
    
    @Value("${github.client-id}")
    private String clientId;
    
    @Value("${github.client-secret}")
    private String clientSecret;
    
    @Value("${github.redirect-uri}")
    private String redirectUri;
    
    @Value("${github.token-uri}")
    private String tokenUri;
    
    @Value("${github.user-info-uri}")
    private String userInfoUri;
    
    private final OkHttpClient client = new OkHttpClient();
    
    @GetMapping("/oauth/callback")
    public String callback(HttpServletRequest req, @RequestParam String code) throws Exception {
        
        HttpSession session = req.getSession();
        
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .build();
        
        Request tokenRequest = new Request.Builder()
                .url(tokenUri)
                .post(formBody)
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
                //System.out.println("userInfoResponse.body().string() = " + result);
                
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);
                
                System.out.println("element = " + element);
                
                JsonObject jsonObject = element.getAsJsonObject();
                
                String login = jsonObject.get("login").getAsString();
                int id = jsonObject.get("id").getAsInt();
                String avatarUrl = jsonObject.get("avatar_url").getAsString();
                String email = jsonObject.has("email") && !jsonObject.get("email").isJsonNull()
                        ? jsonObject.get("email").getAsString()
                        : "Email not available";
                int publicRepos = jsonObject.get("public_repos").getAsInt();
                String createdAt = jsonObject.get("created_at").getAsString();
                String planName = jsonObject.get("plan").getAsJsonObject().get("name").getAsString();
                
                System.out.println("Login::::::::::::::::: " + login);
                System.out.println("ID:::::::::::::::::::: " + id);
                System.out.println("Avatar URL:::::::::::: " + avatarUrl);
                System.out.println("Email::::::::::::::::: " + email);
                System.out.println("Public Repositories::: " + publicRepos);
                System.out.println("Account Created At:::: " + createdAt);
                System.out.println("Plan Name::::::::::::: " + planName);
                
                session.setAttribute("MEMBER_ID", "gh_" + id);
                
                return "redirect:/";
                
            }
        }
    }
}
