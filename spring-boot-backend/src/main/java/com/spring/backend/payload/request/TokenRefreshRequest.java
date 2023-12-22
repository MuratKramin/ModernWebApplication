package com.spring.backend.payload.request;

import javax.validation.constraints.NotBlank;

public class TokenRefreshRequest {
  @NotBlank
  private String refreshToken;

  public TokenRefreshRequest(){

  }

  public TokenRefreshRequest(String valid_refresh_token) {
    refreshToken = valid_refresh_token;
  }

    public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
