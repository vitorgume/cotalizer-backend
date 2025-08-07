package com.gumeinteligenciacomercial.orcaja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
public class OAuth2Config {

    @Bean("defaultOauth2UserService")
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOauth2UserService() {
        return new DefaultOAuth2UserService();
    }
}
