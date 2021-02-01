package sv.ufg.ordenaenlinea.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionHelper {
//    private static final Logger logger = LoggerFactory.getLogger(ExceptionHelper.class);
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxUploadSize;

    @ExceptionHandler(value = {PropertyReferenceException.class})
    public ResponseEntity<ExceptionWrapper> handleBadRequest (
            RuntimeException ex, HttpServletRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return buildValidationResponse(request, status, ex.getMessage(), new ArrayList<>());
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<ExceptionWrapper> handleNotFound (
            RuntimeException ex, HttpServletRequest request) {
        return buildResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    public ResponseEntity<ExceptionWrapper> handleConflict (
            RuntimeException ex, HttpServletRequest request) {
        return buildResponse(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    public ResponseEntity<ExceptionWrapper> handlePayloadTooLarge (
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
            return buildResponse(ex, request, HttpStatus.PAYLOAD_TOO_LARGE,
                    String.format("Tamaño de archivo excede el maximo permitido es %s", maxUploadSize));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionWrapper> handleValidation (
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return buildValidationResponse(request,
                status,
                "Errores de validacion, revise la propiedad fieldErrors",
                fieldErrors);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ExceptionWrapper> genericHandler (
            Throwable ex, HttpServletRequest request) {
        return buildResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionWrapper> buildValidationResponse(HttpServletRequest request,
                                                                               HttpStatus status,
                                                                               String mensaje,
                                                                               List<FieldError> fieldErrors) {
        Map<String, String> errores = new HashMap<>();

        for (FieldError fieldError: fieldErrors) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ExceptionWrapper wrapper = new ExceptionWrapper(
                status.value(),
                mensaje,
                request.getRequestURL().toString(),
                fieldErrors
        );

        return new ResponseEntity<>(wrapper, status);
    }

    private ResponseEntity<ExceptionWrapper> buildResponse(Throwable ex,
                                                           HttpServletRequest request,
                                                           HttpStatus status) {
        return buildResponse(ex, request, status, ex.getMessage());
    }

    private ResponseEntity<ExceptionWrapper> buildResponse(Throwable ex,
                                                           HttpServletRequest request,
                                                           HttpStatus status,
                                                           String message) {
        logError(ex, request);

        ExceptionWrapper wrapper = new ExceptionWrapper(
                status.value(),
                message,
                request.getRequestURL().toString()
        );

        return new ResponseEntity<>(wrapper, status);
    }

    private void logError(Throwable ex, HttpServletRequest request) {
//        String queryString = request.getQueryString();
//        logger.error(String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()));
//        logger.error(String.format("%s: %s%s",
//                request.getMethod(),
//                request.getRequestURL().toString(),
//                queryString == null ? "" : "?" + queryString));
    }
}
