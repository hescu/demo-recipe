package platform.codingnomads.co.demorecipe.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AggregateMissingFieldsException extends Exception {
    private final List<Exception> basket;
    public AggregateMissingFieldsException() {
        this.basket = new ArrayList<>();
    }

    public void addExceptionToBasket(Exception e) {
        basket.add(e);
    }

    public String getCombinedMessage() {
        StringBuilder combinedMessage = new StringBuilder("Multiple exceptions occurred: \n");
        for (Exception e : basket) {
            combinedMessage.append(e.getMessage()).append(";\n");
        }

        if (!basket.isEmpty()) {
            combinedMessage.setLength(combinedMessage.length() - 1);
        }

        return combinedMessage.toString();
    }
}
