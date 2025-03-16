package org.opensbpm.oswd;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

public final class BusinessObject extends AbstractNamed {

    public static BusinessObjectBuilder builder() {
        return new BusinessObjectBuilder();
    }

    private final Collection<Attribute> attributes;

    private BusinessObject() {
        attributes = new ArrayList<>();
    }

    private BusinessObject(String name, Collection<Attribute> attributes) {
        super(name);
        this.attributes = new ArrayList<>(attributes);
    }

    public Collection<Attribute> getAttributes() {
        return unmodifiableCollection(attributes);
    }


    public void accept(OswdVisitor visitor) {
        visitor.visitBusinessObject(this);

        getAttributes().forEach(attribute -> attribute.accept(visitor));
    }

    private BusinessObject copy() {
        return new BusinessObject(getName(), attributes);
    }

    public static class BusinessObjectBuilder extends AbstractBuilder<BusinessObject, BusinessObjectBuilder> {

        public BusinessObjectBuilder() {
            super(new BusinessObject());
        }

        @Override
        protected BusinessObjectBuilder self() {
            return this;
        }


        public BusinessObjectBuilder addAttribute(Attribute attribute) {
            product.attributes.add(attribute);
            return self();
        }

        public BusinessObject build() {
            return product.copy();
        }
    }


}
