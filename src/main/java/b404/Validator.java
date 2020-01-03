package b404;

/**
 * General validator class for initial parameter validation
 */

static class Validator{

    static validateString(String parameterName, String value) throws InvalidFormatException{
        if(value == "" || value == null){
            throw new InvalidFormatException("Invalid " + parameterName);
        }
    }
}