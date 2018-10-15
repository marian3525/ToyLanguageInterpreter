package exceptions;

public class UndefinedVariableException extends Exception {
    private String message;
    public UndefinedVariableException(String message) {
        super(message);
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
