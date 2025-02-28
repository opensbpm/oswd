package org.opensbpm.oswd;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Objects;

public abstract class AbstractStackItem<
        B extends ModelBuilderFactory.ModelBuilder,
        C extends ParserRuleContext>
        implements StackItem<B, C> {

    private final C ctx;

    protected AbstractStackItem(C ctx) {
        this.ctx = Objects.requireNonNull(ctx, "ctx must not be null");
    }

    @Override
    public final C getContext() {
        return ctx;
    }

}
