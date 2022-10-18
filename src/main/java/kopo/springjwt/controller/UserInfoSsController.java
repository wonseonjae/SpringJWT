package kopo.springjwt.controller;

import kopo.springjwt.auth.AuthInfo;
import kopo.springjwt.auth.UserRole;
import kopo.springjwt.dto.UserInfoDTO;
import kopo.springjwt.service.IUserInfoSsService;
import kopo.springjwt.util.CmmUtil;
import kopo.springjwt.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value="/ss")
public class UserInfoSsController {
    private final IUserInfoSsService userInfoSsService;

    private final PasswordEncoder bCryptPasswordEncoder;

    @GetMapping(value="userRegForm")
    public String userRegForm(){

        return "/ss/UserRegForm";
    }

    @RequestMapping(value="insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap model) throws Exception{
        String msg = "";

        UserInfoDTO pDTO = null;

        try {
            String user_id = CmmUtil.nvl(request.getParameter("user_id"));
            String user_name = CmmUtil.nvl(request.getParameter("user_name"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
            String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

            pDTO = new UserInfoDTO();

            pDTO.setUserId(user_id);
            pDTO.setUserName(user_name);
            pDTO.setPassword(bCryptPasswordEncoder.encode(password));
            pDTO.setRoles(UserRole.USER.getValue());
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));
            pDTO.setAddr1(addr1);
            pDTO.setAddr2(addr2);

            int res = userInfoSsService.insertUserInfo(pDTO);

            if (res == 1) {
                msg = "회원가입이 완료되었습니다";
            } else if (res == 2) {
                msg = "이미 가입한 메일입니다.";
            } else {
                msg = "회원가입 오류";
            }
        } catch (Exception e) {

            msg = "실패하였습니다 : " + e;
            e.printStackTrace();
        } finally {
            model.addAttribute("msg", msg);

            model.addAttribute("pDTO", pDTO);

            pDTO = null;
        }
        return "/user/UserRegSuccess";
    }

    @GetMapping(value="loginForm")
    public String loginForm(){

        return "/ss/LoginForm";
    }

    @RequestMapping(value = "loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal AuthInfo authInfo, ModelMap model){

        UserInfoDTO dto = authInfo.getUserInfoDTO();
        String userName = CmmUtil.nvl(dto.getUserName());
        String userId = CmmUtil.nvl(dto.getUserId());

        model.addAttribute("userName", userName);
        model.addAttribute("userId", userId);

        return "/ss/LoginSuccess";
    }

    @RequestMapping(value = "loginFail")
    public String loginFail() {
        return "/ss/LoginFail";
    }

    @GetMapping(value = "logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        new SecurityContextLogoutHandler().logout(
                request, response, SecurityContextHolder.getContext().getAuthentication());
        return "/";
    }
}
