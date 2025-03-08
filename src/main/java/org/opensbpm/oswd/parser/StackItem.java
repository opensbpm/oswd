package org.opensbpm.oswd.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.opensbpm.oswd.ModelBuilderFactory.ModelBuilder;

public interface StackItem<B extends ModelBuilder, C extends ParserRuleContext> {

     C getContext();

     B createBuilder();
}
