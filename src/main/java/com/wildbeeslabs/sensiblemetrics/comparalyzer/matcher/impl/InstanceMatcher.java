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
package com.wildbeeslabs.sensiblemetrics.comparalyzer.matcher.impl;

import com.wildbeeslabs.sensiblemetrics.comparalyzer.matcher.Matcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Custom type instance matcher implementation for instance {@link Object}
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InstanceMatcher extends AbstractMatcher<Object> {

    /**
     * Default matchable class instance {@link Class}
     */
    private final Class<?> matchableClazz;

    /**
     * Default instance matcher constructor with class instance {@link Class}
     *
     * @param clazz The predicate evaluates to true for instances of this class
     *              or one of its subclasses.
     */
    public InstanceMatcher(final Class<?> clazz) {
        this.matchableClazz = matchableClass(clazz);
    }

    private Class<?> matchableClass(final Class<?> expectedClass) {
        if (boolean.class.equals(expectedClass)) return Boolean.class;
        if (byte.class.equals(expectedClass)) return Byte.class;
        if (char.class.equals(expectedClass)) return Character.class;
        if (double.class.equals(expectedClass)) return Double.class;
        if (float.class.equals(expectedClass)) return Float.class;
        if (int.class.equals(expectedClass)) return Integer.class;
        if (long.class.equals(expectedClass)) return Long.class;
        if (short.class.equals(expectedClass)) return Short.class;
        return expectedClass;
    }

    @Override
    public boolean matches(final Object value) {
        if (Objects.isNull(value)) {
            return false;
        }
        if (!getMatchableClazz().isInstance(value)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> getMatcher(final Class<? extends T> type) {
        return (Matcher<T>) new InstanceMatcher(type);
    }
}