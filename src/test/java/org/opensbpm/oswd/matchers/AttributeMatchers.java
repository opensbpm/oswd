package org.opensbpm.oswd.matchers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.opensbpm.oswd.Attribute;
import org.opensbpm.oswd.ScalarAttribute;
import org.opensbpm.oswd.AttributeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;

public class AttributeMatchers {

    public static Matcher<Iterable<? extends Attribute>> containsAttributes(Matcher<? super Attribute> matcher, Matcher<? super Attribute>... additionalMatchers) {
        List<Matcher<? super Attribute>> matchers = new ArrayList<>();
        matchers.add(matcher);
        if(additionalMatchers != null) {
            matchers.addAll(asList(additionalMatchers));
        }
        return contains(matchers);
    }

    public static Matcher<? extends Attribute> isScalarAttribute(String AField, AttributeType text, boolean required, boolean readonly) {
        return allOf(
                isAttributeName(AField),
                isAttributeType(text),
                isRequired(required),
                isReadonly(readonly)
        );
    }


    private static Matcher<ScalarAttribute> isAttributeName(String name) {
        return new CustomTypeSafeMatcher<>("Attribute with name " + name) {
            @Override
            protected boolean matchesSafely(ScalarAttribute attribute) {
                return is(name).matches(attribute.getName());
            }
        };
    }

    private static CustomTypeSafeMatcher<ScalarAttribute> isAttributeType(AttributeType attributeType) {
        return new CustomTypeSafeMatcher<>("Attribute with type " + attributeType) {
            @Override
            protected boolean matchesSafely(ScalarAttribute attribute) {
                return is(attributeType).matches(attribute.getAttributeType());
            }
        };
    }

    private static CustomTypeSafeMatcher<ScalarAttribute> isRequired(boolean required) {
        return new CustomTypeSafeMatcher<>("Attribute with required " + required) {
            @Override
            protected boolean matchesSafely(ScalarAttribute attribute) {
                return is(required).matches(attribute.isRequired());
            }
        };
    }

    private static CustomTypeSafeMatcher<ScalarAttribute> isReadonly(boolean readonly) {
        return new CustomTypeSafeMatcher<>("Attribute with readonly " + readonly) {
            @Override
            protected boolean matchesSafely(ScalarAttribute attribute) {
                return is(readonly).matches(attribute.isReadonly());
            }
        };
    }

    private AttributeMatchers() {
        //avoid instantiation
    }
}
