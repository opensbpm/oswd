package org.opensbpm.oswd;


import static java.util.Objects.requireNonNull;

public abstract class NestedAttribute extends AbstractAttribute {

    protected BusinessObject businessObject;

    protected NestedAttribute() {
        //noop
    }

    protected NestedAttribute(String name, BusinessObject businessObject, boolean required, boolean readonly) {
        super(name, required, readonly);
        this.businessObject = requireNonNull(businessObject, "BusinessObject must not be null");
    }

    public BusinessObject getBusinessObject() {
        return businessObject;
    }

    public static abstract class NestedAttributeBuilder<T extends NestedAttribute, B extends NestedAttributeBuilder<T, B>> extends AbstractAttributeBuilder<T, B> {

        protected NestedAttributeBuilder(T product) {
            super(product);
        }

        public B withBusinessObject(BusinessObject businessObject) {
            product.businessObject = requireNonNull(businessObject, "BusinessObject must not be null");
            return self();
        }

    }
}
