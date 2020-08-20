package tech.vedlabs.errors;

import org.springframework.lang.Nullable;

public interface ExceptionLogger {
    void log(@Nullable Throwable exception);
    enum NoOp implements ExceptionLogger {
        INSTANCE;
        @Override
        public void log(Throwable exception) {
        }
    }
}
