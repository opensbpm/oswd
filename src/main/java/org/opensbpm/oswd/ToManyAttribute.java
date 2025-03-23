package org.opensbpm.oswd;

public class ToManyAttribute extends NestedAttribute {

    public static ToManyAttributeBuilder builder() {
        return new ToManyAttributeBuilder();
    }

    private ToManyAttribute() {
        //noop
    }

    private ToManyAttribute(String name, BusinessObject businessObject, boolean required, boolean readonly) {
        super(name, businessObject, required, readonly);
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitAttribute(this);
    }

    private ToManyAttribute copy() {
        return new ToManyAttribute(getName(), businessObject, isRequired(), isReadonly());
    }

    public static class ToManyAttributeBuilder extends NestedAttributeBuilder<ToManyAttribute, ToManyAttributeBuilder> {

        public ToManyAttributeBuilder() {
            super(new ToManyAttribute());
        }

        @Override
        protected ToManyAttributeBuilder self() {
            return this;
        }

        public ToManyAttribute build() {
            return product.copy();
        }
    }
}
