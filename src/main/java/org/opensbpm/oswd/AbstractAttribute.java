package org.opensbpm.oswd;

public abstract class AbstractAttribute extends AbstractNamed implements Attribute {
    protected boolean required;
    protected boolean readonly;

    protected AbstractAttribute() {
        //noop
    }

    protected AbstractAttribute(String name, boolean required, boolean readonly) {
        super(name);
        this.required = required;
        this.readonly = readonly;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isReadonly() {
        return readonly;
    }


    public abstract static class AbstractAttributeBuilder<T extends AbstractAttribute, B extends AbstractAttributeBuilder<T, B>> extends AbstractBuilder<T, B> {

        protected AbstractAttributeBuilder(T product) {
            super(product);
        }

        public B asRequired() {
            product.required = true;
            return self();
        }

        public B asReadonly() {
            product.readonly = true;
            return self();
        }

    }

}
