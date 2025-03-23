package org.opensbpm.oswd.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ContextStack {
    private final Map<Object, Stack<?>> itemStackMap = new HashMap<>();

    public <T> T push(StackItem<?, T> stackItem) {
        Stack<T> stack = new Stack<>();
        itemStackMap.putIfAbsent(stackItem.getContext(), stack);
        return stack.push(stackItem.getItem());
    }

    public <T> T peek(StackItem<?, T> stackItem) {
        return getStack(stackItem).peek();
    }


    public <T> T pop(StackItem<?, T> stackItem) {
        return getStack(stackItem).pop();
    }

    @SuppressWarnings("unchecked")
    private <T> Stack<T> getStack(StackItem<?, T> stackItem) {
        //cast is safe, cause its only possible to add checked Stack
        return (Stack<T>) itemStackMap.get(stackItem.getContext());
    }

}
