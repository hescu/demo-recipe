package platform.codingnomads.co.demorecipe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import platform.codingnomads.co.demorecipe.models.Recipe;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT r.* FROM Recipe r WHERE :minRating <= (SELECT AVG(rr.rating) FROM Review rr WHERE rr.recipe_id = r.id GROUP BY rr.recipe_id)", nativeQuery = true)
    List<Recipe> findRecipesWithMinAverageRating(@Param("minRating") double minRating);
}
