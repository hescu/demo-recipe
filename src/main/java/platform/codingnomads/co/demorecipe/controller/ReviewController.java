package platform.codingnomads.co.demorecipe.controller;

import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;
import platform.codingnomads.co.demorecipe.exceptions.NoReviewingYourOwnRecipesException;
import platform.codingnomads.co.demorecipe.exceptions.NoSuchRecipeException;
import platform.codingnomads.co.demorecipe.exceptions.NoSuchReviewException;
import platform.codingnomads.co.demorecipe.models.Recipe;
import platform.codingnomads.co.demorecipe.models.Review;
import platform.codingnomads.co.demorecipe.models.securitymodels.CustomUserDetails;
import platform.codingnomads.co.demorecipe.services.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @GetMapping()
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Review> retrievedReviews = reviewService.getAllReviews();
            return ResponseEntity.ok(retrievedReviews);
        } catch (IllegalStateException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        try {
            Review retrievedReview = reviewService.getReviewById(id);
            return ResponseEntity.ok(retrievedReview);
        } catch (IllegalStateException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> getReviewByRecipeId(@PathVariable("recipeId") Long recipeId) {
        try {
            List<Review> reviews = reviewService.getReviewByRecipeId(recipeId);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getReviewByUsername(@PathVariable("username") String username) {
        try {
            List<Review> reviews = reviewService.getReviewByUsername(username);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> postNewReview(@RequestBody Review review,
                                           @PathVariable("recipeId") Long recipeId, Authentication authentication) {
        try {
            review.setUser((CustomUserDetails) authentication.getPrincipal());
            Recipe insertedRecipe = reviewService.postNewReview(review, recipeId);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (NoSuchRecipeException | IllegalStateException |
                 NoReviewingYourOwnRecipesException | AggregateMissingFieldsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Review', 'delete')")
    public ResponseEntity<?> deleteReviewById(@PathVariable("id")Long id) {
        try {
            Review review = reviewService.deleteReviewById(id);
            return ResponseEntity.ok(review);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    @PreAuthorize("hasPermission(#reviewToUpdate.id, 'Review', 'edit')")
    public ResponseEntity<?> updateReviewById(@RequestBody Review reviewToUpdate) {
        try {
            Review review = reviewService.updateReviewById(reviewToUpdate);
            return ResponseEntity.ok(review);
        } catch (NoSuchReviewException | AggregateMissingFieldsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
