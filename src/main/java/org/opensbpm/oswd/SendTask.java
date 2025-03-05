package org.opensbpm.oswd;

public interface SendTask extends Task {

    String getObjectNameReference();

    String getReceiverSubjectName();

    String getProceedTo();
}
