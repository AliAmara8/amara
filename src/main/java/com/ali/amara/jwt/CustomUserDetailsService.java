package com.ali.amara.jwt;

import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<UserEntity> userOptional = userRepository.findByEmailOrPhone(identifier, identifier);

        UserEntity userEntity = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("Utilisateur non trouvé avec : " + identifier)
        );

        return User.builder()
                .username(identifier) // Retourne l'identifiant utilisé (email ou phone)
                .password(userEntity.getPassword())
                .roles(userEntity.getRole())
                .build();
}
}
