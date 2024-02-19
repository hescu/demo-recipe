package platform.codingnomads.co.demorecipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import platform.codingnomads.co.demorecipe.models.securitymodels.CustomUserDetails;
import platform.codingnomads.co.demorecipe.repositories.UserRepo;

@Configuration
public class CustomUserPasswordService implements UserDetailsPasswordService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        CustomUserDetails customUserDetails = (CustomUserDetails) user;
        customUserDetails.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(customUserDetails);
        return customUserDetails;
    }
}
