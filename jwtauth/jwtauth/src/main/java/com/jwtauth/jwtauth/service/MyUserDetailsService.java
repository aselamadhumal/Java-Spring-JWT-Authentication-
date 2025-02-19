package com.jwtauth.jwtauth.service;

import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class MyUserDetailsService implements UserDetailsService {

    //private final PasswordEncoder passwordEncoder;
    private final UserRepository userRipository;

    /*public MyUserDetailsService(PasswordEncoder passwordEncoder, UserRipository userRipository) {
        this.passwordEncoder = passwordEncoder;
        this.userRipository = userRipository;
    }*/

    public MyUserDetailsService( UserRepository userRipository) {

        this.userRipository = userRipository;
    }



    @Override
    //in provider passing the username and then find user and that convert to userDetails
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userData = userRipository.findByUsername(username).orElse(null);

        //if user is not in the db
        if (userData == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDetails user = User.builder()
                .username(userData.getUsername())
                //.password(passwordEncoder.encode("asela"))
                .password(userData.getPassword())
                .build();
        return user;
    }


}
