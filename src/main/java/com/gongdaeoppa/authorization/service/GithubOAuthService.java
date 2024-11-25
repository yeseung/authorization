package com.gongdaeoppa.authorization.service;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubOAuthService {
    
    @Value("${github.client-id}")
    private String clientId;
    
    @Value("${github.client-secret}")
    private String clientSecret;
    
    public boolean revokeGitHubToken(String accessToken) {
        String url = "https://api.github.com/applications/" + clientId + "/token";
        
        OkHttpClient client = new OkHttpClient();
        
        // Basic Auth 헤더 생성
        String credentials = Credentials.basic(clientId, clientSecret);
        
        // Request Body 생성
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{ \"access_token\": \"" + accessToken + "\" }"
        );
        
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .header("Authorization", credentials)
                .header("Accept", "application/vnd.github+json")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Access Token_______________ 삭제 성공");
                return true;
            } else {
                System.out.println("Access Token 삭제 실패: " + response.code());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
