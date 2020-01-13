package b404.utility.customexceptions;

public class UnauthorizedException extends Throwable{
    public UnauthorizedException(String errorMessage){
        super(errorMessage);
    }
}
