package exceptions;

public class RepositoryException extends Exception {
    private String message;
    public RepositoryException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
