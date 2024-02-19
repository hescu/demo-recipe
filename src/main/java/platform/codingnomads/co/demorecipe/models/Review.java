package platform.codingnomads.co.demorecipe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import platform.codingnomads.co.demorecipe.exceptions.AggregateMissingFieldsException;
import platform.codingnomads.co.demorecipe.models.securitymodels.CustomUserDetails;

import javax.persistence.*;
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

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

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
        if (user == null) {
            missingFieldsException.addExceptionToBasket(new IllegalStateException("Username missing!"));
        }
        if (!missingFieldsException.getBasket().isEmpty()) {
            throw missingFieldsException;
        }
    }

    public String getAuthor() {
        return user.getUsername();
    }
}
