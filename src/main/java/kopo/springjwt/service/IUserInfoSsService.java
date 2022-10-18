package kopo.springjwt.service;

import kopo.springjwt.dto.UserInfoDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserInfoSsService extends UserDetailsService {

    int insertUserInfo(UserInfoDTO pDTO) throws Exception;
}
