package dev.hyudoro.pay_my_buddy_service.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http // Disable CSRF and CORS (temporary)
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
             // Defining which routes are public and protected.
            .authorizeHttpRequests(auth -> auth
                                   .requestMatchers("/api/auth/**").permitAll()
                                   .anyRequest().authenticated()
           )
            // Session management, spring creates/reads HttpSession.
            .sessionManagement(session -> session
                               .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(16); // more costful for security.
    }

    @Bean
    public AuthenticationManager authentificationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager(); //We can do this cause, the userdetails/passwordEncoder are already as beans.
    }

}
