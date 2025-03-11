package org.opensbpm.oswd;

public interface SendTask extends Task {

    String getObjectNameReference();

    String getReceiverSubjectName();

    String getProceedTo();

    default void accept(OswdVisitor visitor) {
        visitor.visitSendTask(this);

        visitor.visitProceedTo(getProceedTo());
    }
}
