/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.sensiblemetrics.diffy.common.utils;

import com.wildbeeslabs.sensiblemetrics.diffy.common.property.enumeration.NameableType;
import com.wildbeeslabs.sensiblemetrics.diffy.common.property.enumeration.PropertyType;
import com.wildbeeslabs.sensiblemetrics.diffy.common.property.iface.NamingPredicate;
import com.wildbeeslabs.sensiblemetrics.diffy.common.property.iface.NamingTokenizer;
import com.wildbeeslabs.sensiblemetrics.diffy.common.property.iface.NamingTransformer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wildbeeslabs.sensiblemetrics.diffy.common.utils.TypeUtils.DEFAULT_PRIMITIVE_TYPES;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

/**
 * Property utilities implementation {@link NamingPredicate}, {@link NamingTransformer}, {@link NamingTokenizer}
 */
@Slf4j
@UtilityClass
@SuppressWarnings("unchecked")
public class PropertyUtils {

    /**
     * Default property accessor method prefixes
     */
    public static final String GETTER_ACCESSOR_PREFIX = "get";
    public static final String SETTER_ACCESSOR_PREFIX = "set";
    public static final String BOOLEAN_ACCESSOR_PREFIX = "is";
    public static final String ADD_ACCESSOR_PREFIX = "add";

    /**
     * Default camel case tokenizer pattern {@link Pattern}
     */
    private static final Pattern DEFAULT_CAMEL_CASE_PATTERN = Pattern.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");
    /**
     * Default underscore tokenizer pattern {@link Pattern}
     */
    private static final Pattern DEFAULT_UNDERSCORE_PATTERN = Pattern.compile("_");

    /**
     * Default property getter {@link NamingPredicate}
     */
    public static final NamingPredicate DEFAULT_PROPERTY_GETTER_PREDICATE = (propertyName, propertyType) ->
        PropertyType.FIELD.equals(propertyType)
            || propertyName.startsWith(GETTER_ACCESSOR_PREFIX) && propertyName.length() > 3
            || propertyName.startsWith(BOOLEAN_ACCESSOR_PREFIX) && propertyName.length() > 2;
    /**
     * Default property setter {@link NamingPredicate}
     */
    public static final NamingPredicate DEFAULT_PROPERTY_SETTER_PREDICATE = (propertyName, propertyType) ->
        PropertyType.FIELD.equals(propertyType)
            || propertyName.startsWith(SETTER_ACCESSOR_PREFIX) && propertyName.length() > 3;
    /**
     * Default property adder {@link NamingPredicate}
     */
    public static final NamingPredicate DEFAULT_PROPERTY_ADD_PREDICATE = (propertyName, propertyType) ->
        PropertyType.FIELD.equals(propertyType)
            || propertyName.startsWith(ADD_ACCESSOR_PREFIX) && propertyName.length() > 3;
    /**
     * Default property valid {@link NamingPredicate}
     */
    public static final NamingPredicate DEFAULT_PROPERTY_VALID_PREDICATE = (propertyName, propertyType) -> true;

    /**
     * Default property getter {@link NamingTransformer}
     */
    public static final NamingTransformer DEFAULT_PROPERTY_GETTER_TRANSFORMER = (name, nameableType) -> {
        if (NameableType.METHOD.equals(nameableType)) {
            if (name.startsWith(GETTER_ACCESSOR_PREFIX) && name.length() > 3) {
                return ParserUtils.decapitalize(name.substring(3));
            } else if (name.startsWith(BOOLEAN_ACCESSOR_PREFIX) && name.length() > 2) {
                return ParserUtils.decapitalize(name.substring(2));
            }
        }
        return name;
    };

    public static <T> Map<String, Function> getAccessors(final Class<T> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().startsWith(GETTER_ACCESSOR_PREFIX) && m.getParameterTypes().length == 0)
            .collect(Collectors.toUnmodifiableMap(m -> uncapitalize(m.getName().substring(3)), m -> createGetter(clazz, m), (m1, m2) -> m1));
    }

    public static <T, K, V> Map<String, Function<K, V>> isAccessors(final Class<T> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().startsWith(BOOLEAN_ACCESSOR_PREFIX) && m.getParameterTypes().length == 0)
            .collect(Collectors.toUnmodifiableMap(m -> uncapitalize(m.getName().substring(2)), m -> createGetter(clazz, m), (m1, m2) -> m1));
    }

    public static <T, K, V> Map<String, BiConsumer<K, V>> setAccessors(final Class<T> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().startsWith(SETTER_ACCESSOR_PREFIX) && m.getParameterTypes().length == 1)
            .collect(Collectors.toUnmodifiableMap(m -> uncapitalize(m.getName().substring(3)), m -> createSetter(clazz, m), (m1, m2) -> m1));
    }

    protected static <T, K, V> Function<K, V> createGetter(final Class<T> clazz, final Method method) {
        try {
            final MethodHandles.Lookup caller = MethodHandles.lookup();
            final CallSite site = LambdaMetafactory.metafactory(caller,
                "apply",
                MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class),
                caller.findVirtual(clazz, method.getName(), MethodType.methodType(method.getReturnType())),
                MethodType.methodType(method.getReturnType(), clazz));
            final MethodHandle factory = site.getTarget();
            return (Function) factory.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Can not create getter", t);
        }
    }

    protected static <T, K, V> BiConsumer<K, V> createSetter(final Class<T> clazz, final Method method) {
        final Class valueType = method.getParameterTypes()[0];
        try {
            final MethodHandles.Lookup caller = MethodHandles.lookup();
            final CallSite site = LambdaMetafactory.metafactory(caller,
                "accept",
                MethodType.methodType(BiConsumer.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                caller.findVirtual(clazz, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()[0])),
                MethodType.methodType(void.class, clazz, valueType.isPrimitive() ? DEFAULT_PRIMITIVE_TYPES.get(valueType) : valueType));
            final MethodHandle factory = site.getTarget();
            return (BiConsumer) factory.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Can not create setter", t);
        }
    }

    /**
     * Default property setter {@link NamingTransformer}
     */
    public static final NamingTransformer DEFAULT_PROPERTY_SETTER_TRANSFORMER = (name, nameableType) -> {
        if (NameableType.METHOD.equals(nameableType) && name.startsWith(SETTER_ACCESSOR_PREFIX) && name.length() > 3) {
            return ParserUtils.decapitalize(name.substring(3));
        }
        return name;
    };

    /**
     * Default camelcase property {@link NamingTokenizer}
     */
    public static final NamingTokenizer DEFAULT_CAMELCASE_TOKENIZER = (name, nameableType) -> DEFAULT_CAMEL_CASE_PATTERN.split(name);

    /**
     * Default underscore property {@link NamingTokenizer}
     */
    public static NamingTokenizer DEFAULT_UNDERSCORE_TOKENIZER = (name, nameableType) -> DEFAULT_UNDERSCORE_PATTERN.split(name);

    public static boolean isEmpty(final Object o) {
        if (Objects.nonNull(o)) {
            if (o instanceof Object[]) {
                return ((Object[]) o).length == 0 || (((Object[]) o).length == 1 && isEmpty(((Object[]) o)[0]));
            }
            return ("".equals(valueOf(o)))
                || "null".equals(valueOf(o))
                || (o instanceof Collection && ((Collection) o).size() == 0)
                || (o instanceof Map && ((Map) o).size() == 0);
        }
        return true;
    }
}
