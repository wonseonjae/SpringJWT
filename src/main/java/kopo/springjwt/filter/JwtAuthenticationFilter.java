package kopo.springjwt.filter;

import kopo.springjwt.auth.JwtStatus;
import kopo.springjwt.auth.JwtTokenProvider;
import kopo.springjwt.auth.JwtTokenType;
import kopo.springjwt.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.token.access.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    private final JwtTokenProvider jwtTokenProvider;

    private final List<String> url = Collections.unmodifiableList(
            Arrays.asList(
                    "/ss/loginForm",
                    "/"
            )
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String accessToken = CmmUtil.nvl(jwtTokenProvider.resolveToken(request, JwtTokenType.ACCESS_TOKEN));

            JwtStatus accessTokenStatus = jwtTokenProvider.validateToken(accessToken);

            if (accessTokenStatus == JwtStatus.ACCESS){
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else if (accessTokenStatus == JwtStatus.EXPIRED || accessTokenStatus == JwtStatus.DENIED){
                String refreshToken = CmmUtil.nvl(jwtTokenProvider.resolveToken(request, JwtTokenType.REFRESH_TOKEN));

                JwtStatus refreshTokenStatus = jwtTokenProvider.validateToken(refreshToken);

                if (refreshTokenStatus == JwtStatus.ACCESS) {
                    String userId = CmmUtil.nvl(jwtTokenProvider.getUserId(refreshToken));
                    String userRoles = CmmUtil.nvl(jwtTokenProvider.getUserRoles(refreshToken));

                    String reAccessToken = jwtTokenProvider.createToken(userId, userRoles, JwtTokenType.ACCESS_TOKEN);

                    ResponseCookie cookie = ResponseCookie.from(accessTokenName, "")
                            .maxAge(0)
                            .build();
                    //maxAge()를 0으로 설정하여서 기존에 존재하던 AccessToken 삭제
                    response.setHeader("Set-Cookie", cookie.toString());

                    cookie = null;

                    cookie = ResponseCookie.from(accessTokenName, reAccessToken)
                            .domain("localhost")
                            .path("/")
                            .maxAge(accessTokenValidTime)
                            .httpOnly(true)
                            .build();

                    response.setHeader("Set-Cookie", cookie.toString());

                    Authentication authentication = jwtTokenProvider.getAuthentication(reAccessToken);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (refreshTokenStatus == JwtStatus.EXPIRED) {
                    log.info("refresh token 만료");
                } else {
                    log.info("refresh token 오류");
                }

                filterChain.doFilter(request, response);
            }



    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException{

        return url.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }
}
