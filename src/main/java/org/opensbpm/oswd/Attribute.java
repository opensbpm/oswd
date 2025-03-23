package org.opensbpm.oswd;

public interface Attribute extends  HasName {

     boolean isRequired();

     boolean isReadonly();

    void accept(OswdVisitor visitor);
}
