package com.hostelfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** true = show the real cause in the UI message (handy while developing). Set app.debug-errors=false in production. */
    @Value("${app.debug-errors:true}")
    private boolean debugErrors;

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> dataIntegrity(DataIntegrityViolationException e) {
        log.error("Database rejected the data", e);
        String msg = "Some value is too long or invalid for the database (check name/address length)";
        if (debugErrors) msg += " [" + e.getMostSpecificCause().getMessage() + "]";
        return body(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> missingParam(MissingServletRequestParameterException e) {
        return body(HttpStatus.BAD_REQUEST, "Missing field: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> badType(MethodArgumentTypeMismatchException e) {
        return body(HttpStatus.BAD_REQUEST, "Invalid value for: " + e.getName());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> multipart(MultipartException e) {
        log.error("Upload failed", e);
        return body(HttpStatus.BAD_REQUEST, "Image upload failed: " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> denied(AccessDeniedException e) {
        return body(HttpStatus.FORBIDDEN, "You do not have permission to do that");
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> api(ApiException e) {
        return body(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("Validation failed");
        return body(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> tooLarge(MaxUploadSizeExceededException e) {
        return body(HttpStatus.PAYLOAD_TOO_LARGE, "Image is too large (max 5 MB)");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> other(Exception e) {
        log.error("Unhandled error", e);   // the real cause also shows in the backend console
        if (!debugErrors) return body(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String detail = root.getClass().getSimpleName() + ": " + root.getMessage();
        if (detail.length() > 300) detail = detail.substring(0, 300) + "...";
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong [" + detail + "]");
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", LocalDateTime.now().toString());
        m.put("status", s.value());
        m.put("error", msg);
        return ResponseEntity.status(s).body(m);
    }
}
