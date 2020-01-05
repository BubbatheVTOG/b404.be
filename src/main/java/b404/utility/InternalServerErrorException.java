package b404.utility;

public class InternalServerErrorException extends Throwable {
    public InternalServerErrorException(String errorMessage){
        super(errorMessage);
    }
}
