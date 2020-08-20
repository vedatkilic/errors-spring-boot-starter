package tech.vedlabs.errors;

import org.springframework.http.ResponseEntity;

public interface WebResponseExceptionTranslator<T> {
    ResponseEntity<T> translate(ApiError error) throws Exception;
}