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

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/google")
public class GoogleOAuthCallbackController {
    
    @Value("${google.client-id}")
    private String clientId;
    
    @Value("${google.client-secret}")
    private String clientSecret;
    
    @Value("${google.redirect-uri}")
    private String redirectUri;
    
    @Value("${google.token-uri}")
    private String tokenUri;
    
    @Value("${google.user-info-uri}")
    private String userInfoUri;
    
    private final OkHttpClient client = new OkHttpClient();
    
    @GetMapping("/oauth/callback")
    public String callback(HttpSession session, @RequestParam String code) throws Exception {
        
        RequestBody formBody = new FormBody.Builder()
                .add("code", code)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("redirect_uri", redirectUri)
                .add("grant_type", "authorization_code")
                .build();
        
        Request request = new Request.Builder()
                .url(tokenUri)
                .post(formBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get access token: " + response.body().string());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body().string());
            String accessToken = jsonNode.get("access_token").asText();
            
            session.setAttribute("accessToken", accessToken);
            System.out.println("accessToken ==================== " + accessToken);
            
            Request userInfoRequest = new Request.Builder()
                    .url(userInfoUri)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();
            
            try (Response userInfoResponse = client.newCall(userInfoRequest).execute()) {
                if (!userInfoResponse.isSuccessful()) {
                    throw new RuntimeException("Failed to fetch user info: " + userInfoResponse.body().string());
                }
                
                String result = userInfoResponse.body().string();
                System.out.println("userInfoResponse.body().string() = " + result);
                
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);
                
                System.out.println("element = " + element);
                
                JsonObject jsonObject = element.getAsJsonObject();
                
                String sub = jsonObject.get("sub").getAsString();
                String name = jsonObject.get("name").getAsString();
                String givenName = jsonObject.get("given_name").getAsString();
                String familyName = jsonObject.get("family_name").getAsString();
                String picture = jsonObject.get("picture").getAsString();
                String email = jsonObject.get("email").getAsString();
                boolean emailVerified = jsonObject.get("email_verified").getAsBoolean();
                
                System.out.println("Sub:::::::::::: " + sub);
                System.out.println("Name::::::::::: " + name);
                System.out.println("Given Name::::: " + givenName);
                System.out.println("Family Name:::: " + familyName);
                System.out.println("Picture URL:::: " + picture);
                System.out.println("Email:::::::::: " + email);
                System.out.println("Email Verified: " + emailVerified);
                
                session.setAttribute("MEMBER_ID", "gg_" + sub);
                
//                HttpSession session1 = req.getSession(false);
//                System.out.println("session1 = " + session1.getAttribute("MEMBER_ID"));
                
                return "redirect:/";
            }
            
        }
    }
    
    
    
}
