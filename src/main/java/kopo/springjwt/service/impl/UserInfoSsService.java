package kopo.springjwt.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.jpamongoprj.auth.AuthInfo;
import poly.jpamongoprj.dto.UserInfoDTO;
import poly.jpamongoprj.repository.UserInfoRepository;
import poly.jpamongoprj.repository.entity.UserInfoEntity;
import poly.jpamongoprj.service.IUserInfoSsService;
import poly.jpamongoprj.util.CmmUtil;
import poly.jpamongoprj.util.DateUtil;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("UserInfoSsService")
public class UserInfoSsService implements IUserInfoSsService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.getUserId());
        String userName = CmmUtil.nvl(pDTO.getUserName());
        String password = CmmUtil.nvl(pDTO.getPassword());
        String email = CmmUtil.nvl(pDTO.getEmail());
        String addr1 = CmmUtil.nvl(pDTO.getAddr1());
        String addr2 = CmmUtil.nvl(pDTO.getAddr2());
        String roles = CmmUtil.nvl(pDTO.getRoles());

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if (rEntity.isPresent()) {
            res = 2;
        }else {
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId)
                    .userName(userName)
                    .password(password)
                    .email(email)
                    .addr1(addr1)
                    .addr2(addr2)
                    .regId(userId)
                    .regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId)
                    .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .roles(roles)
                    .build();

            userInfoRepository.save(pEntity);

            rEntity = userInfoRepository.findByUserId(userId);

            if (rEntity.isPresent()) {
                res = 1;
            }else {
                res = 0;
            }
        }
        return res;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfoEntity rEntity = userInfoRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " Not Found User"));
        UserInfoDTO rDTO = new ObjectMapper().convertValue(rEntity, UserInfoDTO.class);

        return new AuthInfo(rDTO);
    }
}
