package org.opensbpm.oswd;

import org.opensbpm.oswd.Subject.Role;
import org.opensbpm.oswd.ReceiveTask.Message;

public interface OswdVisitor {

    void visitProcess(Process process);

    void visitSubject(Subject subject);

    void visitRole(Role role);

    void visitShowTask(ShowTask showTask);

    void visitBusinessObject(BusinessObject businessObject);

    void visitAttribute(Attribute attribute);

    void visitSendTask(SendTask sendTask);

    void visitReceiveTask(ReceiveTask receiveTask);

    void visitMessage(Message message);

    void visitProceedTo(String proceedTo);
}
