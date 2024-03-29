package platform.codingnomads.co.demorecipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;
import platform.codingnomads.co.demorecipe.exceptions.NoReviewingYourOwnRecipesException;
import platform.codingnomads.co.demorecipe.exceptions.NoSuchRecipeException;
import platform.codingnomads.co.demorecipe.exceptions.NoSuchReviewException;
import platform.codingnomads.co.demorecipe.models.Recipe;
import platform.codingnomads.co.demorecipe.models.Review;
import platform.codingnomads.co.demorecipe.repositories.ReviewRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);

        if (review.isEmpty()) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }
        return review.get();
    }

    public ArrayList<Review> getReviewByRecipeId(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        ArrayList<Review> reviews = new ArrayList<>(recipe.getReviews());

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews for this recipe.");
        }
        return reviews;
    }

    public List<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findByUsername(username);

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews could be found for username " + username);
        }

        return reviews;
    }

    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException, NoReviewingYourOwnRecipesException, IllegalStateException, AggregateMissingFieldsException {
        review.validateReview(review.getRating());
        Recipe recipe = recipeService.getRecipeById(recipeId);
        if (checkIfSubmittingReviewOnOwnRecipe(review, recipe)) {
            throw new NoReviewingYourOwnRecipesException("Really?! You're trying to review your own recipe?! Have some class!");
        }
        recipe.getReviews().add(review);
        recipeService.updateRecipe(recipe, false);
        return recipe;
    }

    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);

        if (null == review) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);
        return review;
    }

    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException, AggregateMissingFieldsException {
        try {
            Review review = getReviewById(reviewToUpdate.getId());
        } catch (NoSuchReviewException e) {
            throw new NoSuchReviewException("The review you are trying to update. Maybe you meant to create one? If not," +
                    "please double check the ID you passed in.");
        }
        reviewRepo.save(reviewToUpdate);
        return reviewToUpdate;
    }

    private boolean checkIfSubmittingReviewOnOwnRecipe(Review review, Recipe recipe) {
        return (Objects.equals(review.getAuthor(), recipe.getAuthor()));
    }

    public List<Review> getAllReviews() throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findAll();

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews to be found");
        }

        return reviews;
    }
}
