package org.opensbpm.oswd;

import java.util.Collection;

public interface BusinessObject extends HasName {

    Collection<Attribute> getAttributes();
}
