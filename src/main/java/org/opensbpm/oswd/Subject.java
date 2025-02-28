package org.opensbpm.oswd;

public interface Subject {
    String getName();

    Role getRole();

    public interface Role {
        String getName();
    }
}
