package org.opensbpm.oswd;

import static java.util.Objects.requireNonNull;

public abstract class AbstractNamed implements HasName {
    private String name;

    protected AbstractNamed() {
    }

    protected AbstractNamed(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    void setName(String name) {
        this.name = requireNonNull(name, "Name must not be null");
    }

    public interface ModelBuilder<T extends AbstractNamed> {
        T build();
    }

    public abstract static class AbstractBuilder<T extends AbstractNamed, B extends AbstractBuilder<T, B>> implements ModelBuilder<T> {
        protected final T product;

        protected AbstractBuilder(T product) {
            this.product = requireNonNull(product);
        }

        protected abstract B self();

        public final B withName(String name) {
            product.setName(name);
            return self();
        }

    }

}
