package org.opensbpm.oswd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
        visitor.visitScalarAttribute(this);
    }

    private ScalarAttribute copy() {
        return new ScalarAttribute(getName(), attributeType, isRequired(), isReadonly());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .append("type", attributeType)
                .append("required", isRequired())
                .append("readonly", isReadonly())
                .toString();

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
