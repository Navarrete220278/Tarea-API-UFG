package sv.ufg.ordenaenlinea.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ExceptionWrapper {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Integer status;
    private final String message;
    private final List<ValidationError> fieldErrors = new ArrayList<>();
    private final String path;

    public ExceptionWrapper(Integer status, String message, String path) {
        this(status, message, path, new ArrayList<>());
    }

    public ExceptionWrapper(Integer status, String message, String path, List<FieldError> errors) {
        this.status = status;
        this.message = message;
        this.path = path;
        for (FieldError error: errors) {
            fieldErrors.add(new ValidationError(error.getField(), error.getDefaultMessage()));
        }
    }

    @Getter @Setter @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String message;
    }
}
