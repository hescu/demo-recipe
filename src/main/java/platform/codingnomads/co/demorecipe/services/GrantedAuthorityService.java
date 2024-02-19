package platform.codingnomads.co.demorecipe.services;

import org.springframework.stereotype.Service;
import platform.codingnomads.co.demorecipe.models.securitymodels.Role;

import java.util.ArrayList;

@Service
public class GrantedAuthorityService {

    public ArrayList<Role> getGrantedAuthoritiesByUserId(Long userId) {
        return new ArrayList<>();
    }
}
