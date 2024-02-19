package platform.codingnomads.co.demorecipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.codingnomads.co.demorecipe.models.securitymodels.CustomUserDetails;

public interface UserRepo extends JpaRepository<CustomUserDetails, Long> {

    CustomUserDetails findByUsername(String username);
}
