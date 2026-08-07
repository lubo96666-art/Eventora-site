package bg.softuni.eventora.exception;

public class EventoraAccessDeniedException extends RuntimeException {
    public EventoraAccessDeniedException(String message) {
        super(message);
    }
}
