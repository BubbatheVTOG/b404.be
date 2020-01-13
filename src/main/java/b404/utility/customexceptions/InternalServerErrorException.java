package b404.utility.customexceptions;

public class InternalServerErrorException extends Throwable {
    public InternalServerErrorException(String errorMessage){
        super(errorMessage);
    }
}
