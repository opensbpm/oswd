package org.opensbpm.oswd.matchers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.opensbpm.oswd.Attribute;
import org.opensbpm.oswd.AttributeType;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class AttributeMatchers {


    public static Matcher<Attribute> isAttribute(String AField, AttributeType text, boolean required, boolean readonly) {
        return allOf(
                isAttributeName(AField),
                isAttributeType(text),
                isRequired(required),
                isReadonly(readonly)
        );
    }


    private static CustomTypeSafeMatcher<Attribute> isAttributeName(String name) {
        return new CustomTypeSafeMatcher<>("Attribute with name " + name) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(name).matches(attribute.getName());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isAttributeType(AttributeType attributeType) {
        return new CustomTypeSafeMatcher<>("Attribute with type " + attributeType) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(attributeType).matches(attribute.getAttributeType());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isRequired(boolean required) {
        return new CustomTypeSafeMatcher<>("Attribute with required " + required) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(required).matches(attribute.isRequired());
            }
        };
    }

    private static CustomTypeSafeMatcher<Attribute> isReadonly(boolean readonly) {
        return new CustomTypeSafeMatcher<>("Attribute with readonly " + readonly) {
            @Override
            protected boolean matchesSafely(Attribute attribute) {
                return is(readonly).matches(attribute.isReadonly());
            }
        };
    }

    private AttributeMatchers() {
        //avoid instantiation
    }
}
