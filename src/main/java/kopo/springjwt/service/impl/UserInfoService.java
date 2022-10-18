package kopo.springjwt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import poly.jpamongoprj.dto.UserInfoDTO;
import poly.jpamongoprj.repository.UserInfoRepository;
import poly.jpamongoprj.repository.entity.UserInfoEntity;
import poly.jpamongoprj.service.IUserInfoService;
import poly.jpamongoprj.util.CmmUtil;
import poly.jpamongoprj.util.DateUtil;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("UserInfoService")
public class UserInfoService implements IUserInfoService {

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

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if (rEntity.isPresent()) {
            res = 2;
        }else {
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId).userName(userName).password(password).email(email)
                    .addr1(addr1).addr2(addr2)
                    .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
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
    public int getUserLoginCheck(UserInfoDTO pDTO) throws Exception {

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.getUserId());
        String password = CmmUtil.nvl(pDTO.getPassword());

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserIdAndPassword(userId, password);

        if (rEntity.isPresent()){
            res = 1;
        }
        return res;
    }
}
