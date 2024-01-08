package platform.codingnomads.co.demorecipe.models;

import lombok.*;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private int rating;

    @NotNull
    private String description;

    public void validateReview(int rating) throws AggregateMissingFieldsException{
        AggregateMissingFieldsException missingFieldsException = new AggregateMissingFieldsException();
        if (rating <= 0 || rating > 10) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("Rating must be between 0 and 10!"));
        } else {
            this.rating = rating;
        }
        if (username == null || username.isEmpty()) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("Username missing!"));
        }
        if (!missingFieldsException.getBasket().isEmpty()) {
            throw missingFieldsException;
        }
    }
}
