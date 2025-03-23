package org.opensbpm.oswd;

import static java.util.Objects.requireNonNull;

public final class ScalarAttribute extends AbstractAttribute {
    public static ScalarAttributeBuilder builder() {
        return new ScalarAttributeBuilder();
    }

    private AttributeType attributeType;

    private ScalarAttribute() {
        //noop
    }

    private ScalarAttribute(String name, AttributeType attributeType, boolean required, boolean readonly) {
        super(name,required,readonly);
        this.attributeType = requireNonNull(attributeType, "AttributeType must not be null");
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitAttribute(this);
    }

    private ScalarAttribute copy() {
        return new ScalarAttribute(getName(), attributeType, isRequired(), isReadonly());
    }

    public static class ScalarAttributeBuilder extends AbstractAttributeBuilder<ScalarAttribute, ScalarAttributeBuilder> {

        public ScalarAttributeBuilder() {
            super(new ScalarAttribute());
        }

        @Override
        protected ScalarAttributeBuilder self() {
            return this;
        }

        public ScalarAttributeBuilder withType(AttributeType attributeType) {
            product.attributeType = requireNonNull(attributeType, "AttributeType must not be null");
            return self();
        }

        public ScalarAttribute build() {
            return product.copy();
        }
    }

}
