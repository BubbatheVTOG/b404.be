package b404.utility.customexceptions;

public class BadRequestException extends Throwable{

    public BadRequestException(String errorMessage){
        super(errorMessage);
    }
}
