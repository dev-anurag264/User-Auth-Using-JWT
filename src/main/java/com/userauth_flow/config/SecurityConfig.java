package com.userauth_flow.config;


import com.userauth_flow.filter.JWTfilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JWTfilter jwTfilter;

    private final AuthenticationProvider authenticationProvider;

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth" + "/**",          // /api/v1/auth/register, /api/v1/auth/login
            "/swagger-ui/**",           // Swagger UI HTML
            "/swagger-ui.html",
            "/api-docs/**",             // OpenAPI JSON spec
            "/v3/api-docs/**",          // OpenAPI v3
            "/actuator/health"          // Health check (for load balancers)
    };

    //    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)
//            throws Exception {
//
//        return http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http))
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth


                    .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Admin-only endpoints
                    .requestMatchers("/api/admin"+ "/**")
                    .hasRole("ADMIN")

                    // Patient and Admin for patient-specific endpoints
                    .requestMatchers("/api/users" + "/**")
                    .hasAnyRole("USER", "ADMIN")

                    // All other requests: must be authenticated (any role)
                    // authenticated() = logged in with any role
                    .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider)

            // ─── JWT FILTER PLACEMENT ────────────────────────────────────
            // Insert JwtAuthFilter BEFORE UsernamePasswordAuthenticationFilter
            //
            // WHY before? The filter chain runs in order.
            // JwtAuthFilter sets the Authentication in SecurityContext.
            // UsernamePasswordAuthenticationFilter checks if auth is needed.
            // If JwtAuthFilter already set auth → UPAF sees it and skips.
            // If JwtAuthFilter didn't set auth → UPAF handles it (returns 401).
            .addFilterBefore(jwTfilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
}
