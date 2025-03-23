package org.opensbpm.oswd;

import org.opensbpm.oswd.Subject.Role;
import org.opensbpm.oswd.ReceiveTask.Message;

public interface OswdVisitor {

    void visitProcess(Process process);

    void visitSubject(Subject subject);

    void visitRole(Role role);

    void visitShowTask(ShowTask showTask);

    void visitBusinessObject(BusinessObject businessObject);

    void visitScalarAttribute(ScalarAttribute attribute);

    void visitToOneAttribute(ToOneAttribute attribute);

    void visitToManyAttribute(ToManyAttribute attribute);

    void visitSendTask(SendTask sendTask);

    void visitReceiveTask(ReceiveTask receiveTask);

    void visitMessage(Message message);

    void visitProceedTo(String proceedTo);
}
