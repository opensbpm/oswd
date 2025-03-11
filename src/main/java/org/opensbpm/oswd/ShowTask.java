package org.opensbpm.oswd;

public interface ShowTask extends Task {

    BusinessObject getBusinessObject();

    String getProceedTo();

    default void accept(OswdVisitor visitor) {
        visitor.visitShowTask(this);
        getBusinessObject().accept(visitor);

        visitor.visitProceedTo(getProceedTo());
    }
}
