package org.opensbpm.oswd.model;

public interface OswdVisitor {
    void visitProcess(Process process);

    void visitSubject(Subject subject);

    void visitTask(Task task);

    void visitObject(Object object);

    void visitProceedTo(ProceedTo proceedTo);

    void visitAttribute(Attribute attribute);

    void visitSend(Send send);

    void visitReceive(Receive receive);

    void visitMessage(Message message);
}
