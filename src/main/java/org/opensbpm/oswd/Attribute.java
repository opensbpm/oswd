package org.opensbpm.oswd;

import static java.util.Objects.requireNonNull;

public final class Attribute extends AbstractNamed implements HasName {
    public static AttributeBuilder builder() {
        return new AttributeBuilder();
    }

    private AttributeType attributeType;
    private boolean required;
    private boolean readonly;

    private Attribute() {
        //noop
    }

    private Attribute(String name, AttributeType attributeType, boolean required, boolean readonly) {
        super(name);
        this.attributeType = attributeType;
        this.required = required;
        this.readonly = readonly;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void accept(OswdVisitor visitor) {
        visitor.visitAttribute(this);
    }

    private Attribute copy() {
        return new Attribute(getName(), attributeType, required, readonly);
    }

    public static class AttributeBuilder extends AbstractBuilder<Attribute, AttributeBuilder> {

        public AttributeBuilder() {
            super(new Attribute());
        }

        @Override
        protected AttributeBuilder self() {
            return this;
        }

        public AttributeBuilder withType(AttributeType attributeType) {
            product.attributeType = requireNonNull(attributeType, "attributeType must not be null");
            return self();
        }

        public AttributeBuilder asRequired() {
            product.required = true;
            return self();
        }

        public AttributeBuilder asReadonly() {
            product.readonly = true;
            return self();
        }

        public Attribute build() {
            return product.copy();
        }
    }

}
