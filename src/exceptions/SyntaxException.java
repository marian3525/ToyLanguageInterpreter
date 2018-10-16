package exceptions;

public class SyntaxException extends Exception {
    private String message;
    public SyntaxException(String message) {
        super(message);
        this.message = message;
    }
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
