package org.opensbpm.oswd;

import java.util.Objects;
import java.util.stream.Stream;

public enum AttributeType {

    BOOLEAN("bool"),
    NUMBER("number"),
    DATE("date"),
    TEXT("text");

    private final String token;

    AttributeType(String token) {
        this.token = Objects.requireNonNull(token);
    }

    public String getToken() {
        return token;
    }

    public static AttributeType tokenOf(String token) {
        Objects.requireNonNull(token, "Token is null");
        return Stream.of(AttributeType.values())
                .filter(t -> t.getToken().equals(token))
                .findFirst().orElseThrow();
    }
}
