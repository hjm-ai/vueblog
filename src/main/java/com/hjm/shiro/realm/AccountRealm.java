package com.hjm.shiro.realm;

import cn.hutool.core.bean.BeanUtil;
import com.hjm.entity.User;
import com.hjm.service.UserService;
import com.hjm.shiro.token.JwtToken;
import com.hjm.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {



        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        JwtToken jwtToken= (JwtToken) authenticationToken;
        String userId = jwtUtil.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        User user = userService.getById(userId);
        if (user==null){
            throw new UnknownAccountException("账户不存在");
        }
        if (user.getStatus()==-1){
            throw new LockedAccountException("账户已被锁定");
        }
        AccountProfile profile=new AccountProfile();
        BeanUtil.copyProperties(user,profile);
        return new SimpleAuthenticationInfo(profile,jwtToken.getCredentials(),getName());
    }
}
