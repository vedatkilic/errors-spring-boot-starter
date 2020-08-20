package tech.vedlabs.errors;

import org.springframework.http.ResponseEntity;

public class DefaultWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity translate(ApiError apiError) throws Exception {
        return new ResponseEntity(apiError, apiError.getHttpStatus());
    }
}
