package org.opensbpm.oswd;

import static java.util.Objects.requireNonNull;

public final class ShowTask extends AbstractNamed implements Task {

    public static ShowTaskBuilder builder() {
        return new ShowTaskBuilder();
    }

    private BusinessObject businessObject;
    private String proceedTo;

    private ShowTask() {
        //noop
    }

    private ShowTask(String name, BusinessObject businessObject, String proceedTo) {
        super(name);
        this.businessObject = businessObject;
        this.proceedTo = proceedTo;
    }

    public BusinessObject getBusinessObject() {
        return businessObject;
    }

    public String getProceedTo() {
        return proceedTo;
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitShowTask(this);
        getBusinessObject().accept(visitor);

        visitor.visitProceedTo(getProceedTo());
    }

    private ShowTask copy() {
        return new ShowTask(getName(), businessObject, proceedTo);
    }

    public static class ShowTaskBuilder extends AbstractBuilder<ShowTask, ShowTaskBuilder> {

        private ShowTaskBuilder() {
            super(new ShowTask());
        }

        @Override
        protected ShowTaskBuilder self() {
            return this;
        }

        public ShowTaskBuilder withBusinessObject(BusinessObject businessObject) {
            product.businessObject = requireNonNull(businessObject, "BusinessObject name must not be null");
            return self();
        }


        public ShowTaskBuilder withProceedTo(String taskNameReference) {
            product.proceedTo = requireNonNull(taskNameReference, "taskNameReference must not be null");
            return self();
        }

        public ShowTask build() {
            return product.copy();
        }
    }

}
