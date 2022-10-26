package kopo.springjwt.controller;

import kopo.springjwt.auth.AuthInfo;
import kopo.springjwt.auth.JwtTokenProvider;
import kopo.springjwt.auth.JwtTokenType;
import kopo.springjwt.dto.UserInfoDTO;
import kopo.springjwt.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping(value = "/jwt")
@RequiredArgsConstructor
@Controller
public class JwtController {

    @Value("${jwt.token.access.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.token.access.name}")
    private String accessTokenName;

    @Value("${jwt.token.refresh.valid.time}")
    private long refreshTokenValidTime;

    @Value("${jwt.token.refresh.name}")
    private String refreshTokenName;

    private final JwtTokenProvider jwtTokenProvider;

    @RequestMapping(value = "loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal AuthInfo authInfo,
                               HttpServletResponse response, ModelMap model) throws Exception{

        UserInfoDTO dto = authInfo.getUserInfoDTO();

        if (dto == null) {
            dto = new UserInfoDTO();
        }

        String userId = CmmUtil.nvl(dto.getUserId());
        String userName = CmmUtil.nvl(dto.getUserName());
        String userRoles  = CmmUtil.nvl(dto.getRoles());

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("userRoles : " + userRoles);

        String accessToken = jwtTokenProvider.createToken(userId, userRoles, JwtTokenType.ACCESS_TOKEN);

        ResponseCookie cookie = ResponseCookie.from(accessTokenName, accessToken)
                .domain("localhost")
                .path("/")
                .maxAge(accessTokenValidTime)
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        cookie = null;

        String refreshToken = jwtTokenProvider.createToken(userId, userRoles, JwtTokenType.REFRESH_TOKEN);

        cookie = ResponseCookie.from(accessTokenName, accessToken)
                .domain("localhost")
                .path("/")
                .maxAge(refreshTokenValidTime)
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        model.addAttribute("userName", userName);

        return "/ss/LoginSuccess";
    }

    @RequestMapping(value = "loginFail")
    public String loginFail(){
        return "/ss/LoginFail";
    }

    @GetMapping(value = "logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        new SecurityContextLogoutHandler().logout(
                request, response, SecurityContextHolder.getContext().getAuthentication()
        );
        return "/";

    }
}
