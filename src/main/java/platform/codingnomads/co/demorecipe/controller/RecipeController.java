package platform.codingnomads.co.demorecipe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;
import platform.codingnomads.co.demorecipe.exceptions.NoSuchRecipeException;
import platform.codingnomads.co.demorecipe.models.Recipe;
import platform.codingnomads.co.demorecipe.models.securitymodels.CustomUserDetails;
import platform.codingnomads.co.demorecipe.services.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @PostMapping
    public ResponseEntity<?> createNewRecipe(@RequestBody Recipe recipe, Authentication authentication) {
        try {
            recipe.setUser((CustomUserDetails) authentication.getPrincipal());
            Recipe insertedRecipe = recipeService.createNewRecipe(recipe);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (IllegalStateException | AggregateMissingFieldsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRecipes() {
        try {
            return ResponseEntity.ok(recipeService.getAllRecipes());
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> getRecipesByName(@PathVariable("name") String name) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByName(name);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/username/{username}")
    public ResponseEntity<?> getRecipesByUsername(@PathVariable("username") String username) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByUsername(username);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(value = "/search", params = "minimum_average_rating")
    public ResponseEntity<?> getAllRecipesBasedOnMinimumAverageRating(@RequestParam(name = "minimum_average_rating") double minimumAverageRating) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesWithMinimumAverageRating(minimumAverageRating);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(value = "/search", params = {"name", "max_difficulty_rating"})
    public ResponseEntity<?> getRecipesByNameAndMaxDifficultyRating(@RequestParam(name = "name") String name,
                                                                    @RequestParam(name = "max_difficulty_rating") int maxDifficultyRating) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByNameAndMaxDifficultyRating(name, maxDifficultyRating);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Recipe', 'delete')")
    //make sure that a user is either an admin or the owner of the recipe before they are allowed to delete
    public ResponseEntity<?> deleteRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe deletedRecipe = recipeService.deleteRecipeById(id);
            return ResponseEntity.ok("The recipe with ID " + deletedRecipe.getId() + " and name " + deletedRecipe.getName() + " was deleted");
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    @PreAuthorize("hasPermission(#updatedRecipe.id, 'Recipe', 'edit')")
    public ResponseEntity<?> updateRecipe(@RequestBody Recipe updatedRecipe) {
        try {
            Recipe returnedUpdatedRecipe = recipeService.updateRecipe(updatedRecipe, true);
            return ResponseEntity.ok(returnedUpdatedRecipe);
        } catch (NoSuchRecipeException | IllegalStateException | AggregateMissingFieldsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
