package biojj.portfoliomanagerapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    record ErrorBody(Instant timestamp, int status, String error, String message, String path,
                     Map<String, String> fields) {
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorBody> notFound(NotFoundException e, HttpServletRequest r) {
        return body(HttpStatus.NOT_FOUND, e, r, Map.of());
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorBody> business(BusinessException e, HttpServletRequest r) {
        return body(HttpStatus.UNPROCESSABLE_ENTITY, e, r, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        Map<String, String> f = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(x -> f.put(x.getField(), x.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, e, r, f);
    }

    private ResponseEntity<ErrorBody> body(HttpStatus s, Exception e, HttpServletRequest r, Map<String, String> f) {
        return ResponseEntity.status(s).body(new ErrorBody(Instant.now(), s.value(), s.getReasonPhrase(), e.getMessage(), r.getRequestURI(), f));
    }
}
