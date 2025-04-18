package org.opensbpm.oswd.model;

public class ProceedTo {
    String name;
    Object object;

    ProceedTo(String name) {
        this.name = name
    }

    void accept(OswdVisitor visitor) {
        visitor.visitProceedTo(this)
        if(object != null) {
            object.accept(visitor)
        }
    }
}
