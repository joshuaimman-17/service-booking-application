package com.team10.servicebooking.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import com.team10.servicebooking.login.repository.UserRepository;
import com.team10.servicebooking.login.service.UserDetailsServiceImpl;
import com.team10.servicebooking.login.model.Users;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("home-client")
                .clientSecret("{noop}home-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8082/login/oauth2/code/home-client")
                .scope("read")
                .build();

        RegisteredClient providerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("provider-client")
                .clientSecret("{noop}provider-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8084/login/oauth2/code/provider-client")
                .scope("provider")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient, providerClient);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpForm -> {
                    httpForm.loginPage("/auth/login").permitAll();
                    httpForm.loginProcessingUrl("/login");
                    httpForm.successHandler(customAuthenticationSuccessHandler());
                    httpForm.failureUrl("/auth/login?error=true");
                })
                .logout(logout -> {
                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
                    logout.logoutSuccessUrl("/auth/login?logout=true");
                    logout.deleteCookies("JSESSIONID");
                    logout.invalidateHttpSession(true);
                })
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                            "/auth/login",
                            "/auth/signup",
                            "/auth/register",
                            "/auth/verify",
                            "/auth/forgot-password",
                            "/auth/reset-password"
                    ).permitAll();

                    registry.requestMatchers(
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/webjars/**"
                    ).permitAll();

                    registry.requestMatchers("/provider/**").hasAuthority("PROVIDER");
                    registry.anyRequest().authenticated();
                });

        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                Optional<Users> userOptional = userRepository.findByUsername(username);

                if (userOptional.isPresent()) {
                    String role = userOptional.get().getRole();

                    switch (role.toUpperCase()) {
                        case "ADMIN" -> response.sendRedirect("http://localhost:8083/admin/dashboard");
                        case "PROVIDER" -> response.sendRedirect("http://localhost:8084/provider/dashboard");
                        default -> response.sendRedirect("http://localhost:8082/services");
                    }
                    return;
                }
            }
            response.sendRedirect("/auth/login?error=role-redirect-failed");
        };
    }
}
