package org.opensbpm.oswd;


import static java.util.Objects.requireNonNull;

public class ToOneAttribute extends NestedAttribute {

    public static ToOneAttributeBuilder builder() {
        return new ToOneAttributeBuilder();
    }

    private ToOneAttribute() {
        //noop
    }

    private ToOneAttribute(String name, BusinessObject businessObject, boolean required, boolean readonly) {
        super(name, businessObject, required, readonly);
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitToOneAttribute(this);
    }

    private ToOneAttribute copy() {
        return new ToOneAttribute(getName(), businessObject, isRequired(), isReadonly());
    }

    public static class ToOneAttributeBuilder extends NestedAttributeBuilder<ToOneAttribute, ToOneAttributeBuilder> {

        public ToOneAttributeBuilder() {
            super(new ToOneAttribute());
        }

        @Override
        protected ToOneAttributeBuilder self() {
            return this;
        }

        public ToOneAttribute build() {
            return product.copy();
        }
    }
}
