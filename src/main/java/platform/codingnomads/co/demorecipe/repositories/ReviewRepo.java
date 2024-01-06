package platform.codingnomads.co.demorecipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.codingnomads.co.demorecipe.models.Review;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {

    List<Review> findByUsername(String username);
}
