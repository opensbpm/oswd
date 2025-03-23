package org.opensbpm.oswd.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.opensbpm.oswd.*;
import org.opensbpm.oswd.Process;
import org.opensbpm.oswd.context.StackItem;
import org.opensbpm.oswd.parser.OswdParser.AttributeContext;
import org.opensbpm.oswd.parser.OswdParser.ProcessContext;
import org.opensbpm.oswd.parser.OswdParser.SubjectContext;
import org.opensbpm.oswd.parser.OswdParser.ShowContext;
import org.opensbpm.oswd.parser.OswdParser.SendContext;
import org.opensbpm.oswd.parser.OswdParser.ReceiveContext;
import org.opensbpm.oswd.parser.OswdParser.ObjectContext;
import org.opensbpm.oswd.Attribute.AttributeBuilder;
import org.opensbpm.oswd.Process.ProcessBuilder;
import org.opensbpm.oswd.Subject.SubjectBuilder;
import org.opensbpm.oswd.ShowTask.ShowTaskBuilder;
import org.opensbpm.oswd.SendTask.SendTaskBuilder;
import org.opensbpm.oswd.ReceiveTask.ReceiveTaskBuilder;
import org.opensbpm.oswd.BusinessObject.BusinessObjectBuilder;

import java.util.Objects;

class ContextStackFactory {

    public static StackItem<ProcessContext, ProcessBuilder> processItem(ProcessContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ProcessBuilder getItem() {
                return Process.builder();
            }
        };
    }

    public static StackItem<SubjectContext, SubjectBuilder> subjectItem(SubjectContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public SubjectBuilder getItem() {
                return Subject.builder();
            }

        };
    }

    public static StackItem<ShowContext, ShowTaskBuilder> showItem(ShowContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ShowTaskBuilder getItem() {
                return ShowTask.builder();
            }
        };
    }

    public static StackItem<SendContext, SendTaskBuilder> sendItem(SendContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public SendTaskBuilder getItem() {
                return SendTask.builder();
            }

        };
    }

    public static StackItem<ReceiveContext, ReceiveTaskBuilder> receiveItem(ReceiveContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public ReceiveTaskBuilder getItem() {
                return ReceiveTask.builder();
            }

        };
    }

    public static StackItem<ObjectContext, BusinessObjectBuilder> objectItem(ObjectContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public BusinessObjectBuilder getItem() {
                return BusinessObject.builder();
            }

        };
    }

    public static StackItem<AttributeContext, AttributeBuilder> attributeItem(AttributeContext ctx) {
        return new AbstractStackItem<>(ctx) {

            @Override
            public AttributeBuilder getItem() {
                return Attribute.builder();
            }

        };
    }

    private ContextStackFactory() {
        //avoid instantiation
    }

    public abstract static class AbstractStackItem<
            B extends AbstractNamed.ModelBuilder<?>,
            C extends ParserRuleContext>
            implements StackItem<C, B> {

        private final C ctx;

        protected AbstractStackItem(C ctx) {
            this.ctx = Objects.requireNonNull(ctx, "ctx must not be null");
        }

        @Override
        public final C getContext() {
            return ctx;
        }

    }
}
