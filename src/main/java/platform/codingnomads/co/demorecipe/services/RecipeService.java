package platform.codingnomads.co.demorecipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;
import platform.codingnomads.co.demorecipe.exceptions.NoSuchRecipeException;
import platform.codingnomads.co.demorecipe.models.Recipe;
import platform.codingnomads.co.demorecipe.models.securitymodels.CustomUserDetails;
import platform.codingnomads.co.demorecipe.repositories.RecipeRepo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe) throws AggregateMissingFieldsException {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        recipe.setUser(userDetails);
        recipe.validateRecipe();
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        return recipe;
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();
        recipe.generateLocationURI();
        return recipe;
    }

    public List<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCase(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name.");
        }

        for (Recipe r : matchingRecipes) {
            r.generateLocationURI();
        }

        return matchingRecipes;
    }

    public List<Recipe> getRecipesByUsername(String username) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByUsernameContainingIgnoreCase(username);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes created by that username could be found.");
        }

        return matchingRecipes;
    }

    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }
        return recipes;
    }

    public List<Recipe> getRecipesWithMinimumAverageRating(double minimumAverageRating) throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findRecipesWithMinAverageRating(minimumAverageRating);

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes with an average rating above " + minimumAverageRating + ".");
        }
        return recipes;
    }

    public List<Recipe> getRecipesByNameAndMaxDifficultyRating(String name, int maxDifficultyRating) throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findByNameContainingAndDifficultyRatingLessThanEqual(name, maxDifficultyRating);

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes with name: " + name + " and a maximum difficulty rating of " + maxDifficultyRating);
        }
        return recipes;
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException, AggregateMissingFieldsException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validateRecipe();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("The recipe you passed in did not have an ID found in the database." +
                    " Double check that it is correct. Or maybe you meant to POST a recipe not PATCH one.");
        }
    }
}
