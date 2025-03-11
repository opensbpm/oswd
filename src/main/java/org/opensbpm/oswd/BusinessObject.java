package org.opensbpm.oswd;

import java.util.Collection;

public interface BusinessObject extends HasName {

    Collection<Attribute> getAttributes();

    default void accept(OswdVisitor visitor) {
        visitor.visitBusinessObject(this);

        getAttributes().forEach(attribute -> attribute.accept(visitor));
    }
}
