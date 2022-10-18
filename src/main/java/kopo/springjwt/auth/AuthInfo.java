package kopo.springjwt.auth;

import kopo.springjwt.dto.UserInfoDTO;
import kopo.springjwt.util.CmmUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@AllArgsConstructor
public class AuthInfo implements UserDetails {

    private final UserInfoDTO userInfoDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> pSet = new HashSet<>();

        String roles = CmmUtil.nvl(userInfoDTO.getRoles());

        if (roles.length() > 0){
            for (String role : roles.split(",")){
                pSet.add(new SimpleGrantedAuthority(role));
            }
        }
        return pSet;
    }

    @Override
    public String getPassword() {
        return CmmUtil.nvl(userInfoDTO.getPassword());
    }

    @Override
    public String getUsername() {
        return CmmUtil.nvl(userInfoDTO.getUserId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
