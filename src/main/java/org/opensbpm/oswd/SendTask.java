package org.opensbpm.oswd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


import static java.util.Objects.requireNonNull;

public final class SendTask extends AbstractNamed implements Task {

    public static SendTaskBuilder builder() {
        return new SendTaskBuilder();
    }

    private String objectNameReference;
    private String receiverSubjectName;
    private String proceedTo;

    private SendTask() {
        //noop
    }

    public SendTask(String name, String objectNameReference, String receiverSubjectName, String proceedTo) {
        super(name);
        this.objectNameReference = requireNonNull(objectNameReference, "ObjectNameReference must not be null");
        this.receiverSubjectName = requireNonNull(receiverSubjectName, "ReceiverSubjectName must not be null");
        this.proceedTo = proceedTo;
    }

    public String getObjectNameReference() {
        return objectNameReference;
    }

    public String getReceiverSubjectName() {
        return receiverSubjectName;
    }

    public String getProceedTo() {
        return proceedTo;
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitSendTask(this);

        visitor.visitProceedTo(getProceedTo());
    }

    private SendTask copy() {
        return new SendTask(getName(), objectNameReference, receiverSubjectName, proceedTo);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .append("objectNameReference", objectNameReference)
                .append("receiverSubjectName", receiverSubjectName)
                .append("proceedTo", proceedTo)
                .toString();
    }

    public static class SendTaskBuilder extends TaskBuilder<SendTask, SendTaskBuilder> {
        private SendTaskBuilder() {
            super(new SendTask());
        }

        @Override
        protected SendTaskBuilder self() {
            return this;
        }

        public SendTaskBuilder withObjectNameReference(String objectNameReference) {
            product.objectNameReference = requireNonNull(objectNameReference, "objectNameReference must not be null");
            return self();
        }

        public SendTaskBuilder withReceiverSubjectName(String receiverSubjectName) {
            product.receiverSubjectName = requireNonNull(receiverSubjectName, "receiverSubjectName must not be null");
            return self();
        }

        public SendTaskBuilder withProceedTo(String taskName) {
            product.proceedTo = requireNonNull(taskName, "taskName must not be null");
            return self();
        }

        public SendTask build() {
            return product.copy();
        }
    }

}
