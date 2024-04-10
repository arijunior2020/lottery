package com.lottery.marketplace.domain.auth;

import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
 
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
  
        User user = userService.findByEmail(email);
 
        return new UserPrincipal(user);
    }
}