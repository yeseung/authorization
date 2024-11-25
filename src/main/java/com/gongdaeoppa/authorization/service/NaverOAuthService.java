package com.gongdaeoppa.authorization.service;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverOAuthService {
    
    @Value("${naver.client-id}")
    private String clientId;
    
    @Value("${naver.client-secret}")
    private String clientSecret;
    
    @Value("${naver.token-uri}")
    private String tokenUri;

    
    public String deleteToken(String accessToken) {
        String deleteUrl = tokenUri + "?grant_type=delete" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&access_token=" + accessToken +
                "&service_provider=NAVER"; // 네이버 지정 필요
        return deleteUrl;
    }
    
    
    public boolean deleteAccessToken(String deleteUrl) {
        OkHttpClient client = new OkHttpClient();
        
        Request request = new Request.Builder()
                .url(deleteUrl)
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (((Response) response).isSuccessful()) {
                System.out.println("Response: " + response.body().string());
                return true;
            } else {
                System.out.println("Response Error: " + response.code());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
