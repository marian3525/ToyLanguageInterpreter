package exceptions;

public class UndefinedOperationException extends Exception {
    private String message;
    public UndefinedOperationException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
