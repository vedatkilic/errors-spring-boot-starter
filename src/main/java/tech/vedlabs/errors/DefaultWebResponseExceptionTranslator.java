package tech.vedlabs.errors;

import org.springframework.http.ResponseEntity;

public class DefaultWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity translate(ExceptionResponse exceptionResponse) throws Exception {
        return new ResponseEntity(exceptionResponse, exceptionResponse.getStatusCode());
    }
}
