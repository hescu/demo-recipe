package platform.codingnomads.co.demorecipe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer minutesToMake;

    @Column(nullable = false)
    private Integer difficultyRating;

    @Column(nullable = false)
    private String username;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Step> steps = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Review> reviews;

    @Transient
    @JsonIgnore
    private URI locationURI;

    public void setDifficultyRating(int difficultyRating) {
        if (difficultyRating < 0 || difficultyRating > 10) {
            throw new IllegalStateException("Difficulty rating must be between 0 and 10.");
        }
        this.difficultyRating = difficultyRating;
    }

    public void validateRecipe() throws AggregateMissingFieldsException {
        AggregateMissingFieldsException missingFieldsException = new AggregateMissingFieldsException();
        if (ingredients == null || ingredients.isEmpty()) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("You have to have at least one ingredient for your recipe!"));
        }
        if (steps == null || steps.isEmpty()) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("You have to include at least one step for your recipe!"));
        }
        if (name == null || name.isEmpty()) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("Recipe name missing!"));
        }
        if (username == null || username.isEmpty()) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("Username missing!"));
        }
        if (!missingFieldsException.getBasket().isEmpty()) {
            throw missingFieldsException;
        }
    }

    public void generateLocationURI() {
        try {
            locationURI = new URI(
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/recipes/")
                            .path(String.valueOf(id))
                            .toUriString());
        } catch (URISyntaxException e) {
            //Exception should stop here.
        }
    }

    public double calculateAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }
}
