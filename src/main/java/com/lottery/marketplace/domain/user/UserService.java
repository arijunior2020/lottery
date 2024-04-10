package com.lottery.marketplace.domain.user;

import com.lottery.marketplace.domain.auth.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "UserService:findByEmail", key = "#email")
    public User findByEmail(final String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserEmailOpt(String email){
      return userRepository.findByUserEmailOpt(email);
    }

    @CacheEvict(value = "UserService:findByEmail", key = "#user.email")
    public User save(User user){
        return userRepository.save(user);
    }

  public JwtResponse mountLoginResponse(String email, String jwt) {
    User user = userRepository.findByEmail(email);
    JwtResponse response = new JwtResponse();
    response.setId_user(user.getId());
    response.setName(user.getName() + " " + user.getLastName());
    response.setEmail(user.getEmail());
    response.setRole(user.getRole());
    response.setToken(jwt);
    return response;
  }
}