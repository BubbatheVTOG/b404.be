package b404.utility;

public class BadRequestException extends Throwable{

    public BadRequestException(String errorMessage){
        super(errorMessage);
    }
}
