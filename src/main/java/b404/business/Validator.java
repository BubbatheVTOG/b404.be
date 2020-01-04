package main.java.b404.business;

import java.util.IllegalFormatException;

/**
 * General validator class for initial parameter validation
 */

public class Validator{

    public static void validateString(String parameterName, String value) throws IllegalArgumentException {
        if(value == "" || value == null){
            throw new IllegalArgumentException("Invalid " + parameterName);
        }
    }
}