package org.opensbpm.oswd.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.opensbpm.oswd.AbstractNamed.ModelBuilder;

public class ContextStack {
    private final Map<ParserRuleContext, Stack<? extends ModelBuilder<?>>> classStackMap = new HashMap<>();

    public <B extends ModelBuilder<?>> B push(StackItem<B, ? extends ParserRuleContext> stackItem) {
        Stack<B> stack = new Stack<>();
        classStackMap.putIfAbsent(stackItem.getContext(), stack);
        return stack.push(stackItem.createBuilder());
    }

    public <B extends ModelBuilder<?>> B peek(StackItem<B, ? extends ParserRuleContext> stackItem) {
        return getStack(stackItem).peek();
    }


    public <B extends ModelBuilder<?>> B pop(StackItem<B, ? extends ParserRuleContext> stackItem) {
        return getStack(stackItem).pop();
    }

    @SuppressWarnings("unchecked")
    private <B extends ModelBuilder<?>> Stack<B> getStack(StackItem<B, ? extends ParserRuleContext> stackItem) {
        //cast is safe, cause its only possible to add checked Stack
        return (Stack<B>) classStackMap.get(stackItem.getContext());
    }

}
