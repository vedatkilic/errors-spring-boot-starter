package tech.vedlabs.errors;

import lombok.Data;

@Data
public class Argument {
    /**
     * Name of the argument.
     */
    private final String name;

    /**
     * Value of the argument.
     */
    private final Object value;

    private Argument(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public static Argument arg(String name, Object value) {
        return new Argument(name, value);
    }
}
