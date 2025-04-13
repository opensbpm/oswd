package org.opensbpm.oswd.model

import java.util.stream.Stream

enum AttributeType {

    BOOLEAN("bool"),
    NUMBER("number"),
    DATE("date"),
    TIME("time"),
    TEXT("text"),
    BINARY("binary")

    private final String token

    AttributeType(String token) {
        this.token = Objects.requireNonNull(token)
    }

    String getToken() {
        return token
    }

    static AttributeType tokenOf(String token) {
        Objects.requireNonNull(token, "Token is null")
        return Stream.of(values())
                .filter(t -> t.getToken().equals(token))
                .findFirst().orElseThrow()
    }
}
