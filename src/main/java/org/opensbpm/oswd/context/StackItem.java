package org.opensbpm.oswd.context;

public interface StackItem<C, T> {

     C getContext();

     T getItem();
}
