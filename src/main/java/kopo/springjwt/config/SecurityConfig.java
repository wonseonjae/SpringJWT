package kopo.springjwt.config;

import kopo.springjwt.auth.JwtTokenProvider;
import kopo.springjwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.csrf().disable();

        http.authorizeHttpRequests(authz -> authz
                .antMatchers("/user/**","/notice/**").hasAnyAuthority("ROLE_USER")
                .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                .anyRequest().permitAll()
        )
                .formLogin(login -> login
                        .loginPage("/ss/loginForm")
                        .loginProcessingUrl("/ss/loginProc")
                        .usernameParameter("user_id")
                        .passwordParameter("password")

                        .successForwardUrl("/jwt/loginSuccess")
                        .failureForwardUrl("/jwt/loginFail")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
