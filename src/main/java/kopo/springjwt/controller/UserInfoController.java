package kopo.springjwt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import kopo.springjwt.dto.UserInfoDTO;
import kopo.springjwt.service.IUserInfoService;
import kopo.springjwt.util.CmmUtil;
import kopo.springjwt.util.EncryptUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequestMapping(value = "user")
public class UserInfoController {

    @Resource(name="UserInfoService")
    private IUserInfoService userInfoService;

    @GetMapping(value = "userRegForm")
    public String userRegForm() {

        return "/user/UserRegForm";
    }

    @PostMapping(value = "insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap model) throws Exception {

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
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));
            pDTO.setAddr1(addr1);
            pDTO.setAddr2(addr2);

            int res = userInfoService.insertUserInfo(pDTO);

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

    @GetMapping(value = "loginForm")
    public String loginForm() {

        return "/user/LoginForm";
    }

    @PostMapping(value = "getUserLoginCheck")
    public String getUserLoginCheck(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception{

        int res = 0;

        UserInfoDTO pDTO = null;

        try {
            String userId= CmmUtil.nvl(request.getParameter("user_id"));
            String password= CmmUtil.nvl(request.getParameter("password"));

            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);

            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            res = userInfoService.getUserLoginCheck(pDTO);

            if (res == 1){
                session.setAttribute("SS_USER_ID", userId);

            }
        }catch (Exception e) {
            res = 2;
            e.printStackTrace();
        }finally {
            model.addAttribute("res", String.valueOf(res));
            pDTO = null;
        }
        return "/user/LoginResult";
    }
}
