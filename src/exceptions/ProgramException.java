package exceptions;

public class ProgramException extends Exception {
    private String message;
    public ProgramException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
