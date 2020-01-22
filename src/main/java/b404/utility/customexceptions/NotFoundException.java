package b404.utility.customexceptions;

public class NotFoundException extends Throwable {
    public NotFoundException(String errorMessage){
        super(errorMessage);
    }
}
