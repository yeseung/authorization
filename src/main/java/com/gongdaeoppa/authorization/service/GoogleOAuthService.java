package com.gongdaeoppa.authorization.service;


import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {
    
    @Value("${google.revoke-uri}")
    private String revokeUri;

    
    public boolean revokeAccessToken(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        
        RequestBody body = new FormBody.Builder()
                .add("token", accessToken)
                .build();
        
        Request request = new Request.Builder()
                .url(revokeUri)
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Access Token 무효화 성공_");
                return true;
            } else {
                System.out.println("Access Token 무효화 실패: " + response.code());
                System.out.println("Response Body: " + response.body().string());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    

}
