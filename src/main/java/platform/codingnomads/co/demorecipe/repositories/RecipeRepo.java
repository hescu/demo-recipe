package platform.codingnomads.co.demorecipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.codingnomads.co.demorecipe.models.Recipe;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);
}
