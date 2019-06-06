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
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.iface;

import com.wildbeeslabs.sensiblemetrics.diffy.common.entry.iface.Entry;
import com.wildbeeslabs.sensiblemetrics.diffy.exception.BiMatchOperationException;
import com.wildbeeslabs.sensiblemetrics.diffy.exception.InvalidParameterException;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.description.iface.MatchDescription;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.enumeration.BiMatcherModeType;
import lombok.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.wildbeeslabs.sensiblemetrics.diffy.utility.ServiceUtils.listOf;
import static com.wildbeeslabs.sensiblemetrics.diffy.utility.ServiceUtils.reduceOrThrow;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Binary matcher interface declaration {@link BaseMatcher}
 *
 * @param <T> type of input element to be matched by operation
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@FunctionalInterface
public interface BiMatcher<T> extends BaseMatcher<T> {

    /**
     * Default true {@link BiMatcher}
     */
    BiMatcher<?> DEFAULT_TRUE_MATCHER = (value1, value2) -> true;
    /**
     * Default false {@link BiMatcher}
     */
    BiMatcher<?> DEFAULT_FALSE_MATCHER = (value1, value2) -> false;
    /**
     * Default null {@link BiMatcher}
     */
    BiMatcher<?> DEFAULT_NULL_MATCHER = (value1, value2) -> Objects.isNull(value1) && Objects.isNull(value2);
    /**
     * Default identity {@link BiMatcher}
     */
    BiMatcher<?> DEFAULT_IDENTITY_MATCHER = (value1, value2) -> Objects.deepEquals(value1, value2);
    /**
     * Default exception {@link BiMatcher}
     */
    BiMatcher<?> DEFAULT_EXCEPTION_MATCHER = (value1, value2) -> {
        throw new BiMatchOperationException();
    };

    /**
     * Compares provided objects by equality constraint
     *
     * @param first - initial input first value {@code T}
     * @param last  - initial input last value {@code T}
     * @return true - if objects {@code T} are equal, false - otherwise
     */
    boolean matches(final T first, final T last);

    /**
     * Returns {@link BiMatcherModeType}
     *
     * @return {@link BiMatcherModeType}
     */
    @NonNull
    @Override
    default BiMatcherModeType getMode() {
        return BiMatcherModeType.STRICT;
    }

    /**
     * Returns {@link Collection} of {@link Entry}s by input {@link BiMatcher}
     *
     * @param <T>     type of input element to be matched by operation {#link filter}
     * @param values  - initial input {@link Iterable} collection of {@link Entry}s
     * @param matcher - initial input {@link BiMatcher}
     * @return {@link Collection} of {@link Entry}s
     */
    static <T> Collection<Entry<T, T>> matchIf(final Iterable<Entry<T, T>> values, final BiMatcher<T> matcher) {
        return listOf(values).stream().filter((entry -> matcher.matches(entry.getFirst(), entry.getLast()))).collect(Collectors.toList());
    }

    /**
     * Returns negated {@link BiMatcher} operator
     *
     * @return negated {@link BiMatcher} operator
     */
    @NonNull
    default BiMatcher<T> negate() {
        return (final T t1, final T t2) -> !matches(t1, t2);
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "AND" of current predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if {@code other} is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0		0
     *                              0		1		0
     *                              1	 	0		0
     *                              1		1		1
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> and(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (final T t1, final T t2) -> matches(t1, t2) && other.matches(t1, t2);
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "NOT" of this predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if other is {@code null}
     *                              <p>
     *                              Input 1	Output
     *                              0		1
     *                              1 		0
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> not(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (BiMatcher<T>) other.negate();
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "OR" of this predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if other is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0		0
     *                              0 		1		1
     *                              1		0 		1
     *                              1		1		1
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> or(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (final T t1, final T t2) -> matches(t1, t2) || other.matches(t1, t2);
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "XOR" of this predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if other is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0		0
     *                              0		1		1
     *                              1		0 		1
     *                              1		1		0
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> xor(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (final T t1, final T t2) -> matches(t1, t2) ^ other.matches(t1, t2);
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "NAND" of this predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if other is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0 		0 		1
     *                              0 		1		1
     *                              1		0 		1
     *                              1		1	 	0
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> nand(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (final T t1, final T t2) -> not(and(other)).matches(t1, t2);
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "NOR" of this predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if other is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0 	 	1
     *                              0 		1		0
     *                              1		0		0
     *                              1		1		0
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> nor(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (final T t1, final T t2) -> not(or(other)).matches(t1, t2);
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "XNOR" of this predicate and another
     *
     * @param other - initial input {@link BiMatcher} operator to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if other is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0 	 	1
     *                              0 		1	 	0
     *                              1		0		0
     *                              1		1		1
     *                              </p>
     */
    @NonNull
    default BiMatcher<T> xnor(final BiMatcher<? super T> other) {
        Objects.requireNonNull(other, "BiMatcher should not be null!");
        return (final T t1, final T t2) -> not(xor(other)).matches(t1, t2);
    }

    /**
     * Returns binary flag based on all-match input collection of {@link Iterable} collection of {@link Entry} values
     *
     * @param values - initial input {@link Iterable} collection of {@link Entry} values
     * @return true - if all input values {@link Entry} match, false - otherwise
     */
    default boolean allMatch(final Iterable<Entry<T, T>> values) {
        return listOf(values).stream().allMatch(v -> this.matches(v.getFirst(), v.getLast()));
    }

    /**
     * Returns binary flag based on non-match input collection of {@link Iterable} collection of {@link Entry} values
     *
     * @param values - initial input {@link Iterable} collection of {@link Entry} values
     * @return true - if all input values {@link Entry} match, false - otherwise
     */
    default boolean noneMatch(final Iterable<Entry<T, T>> values) {
        return listOf(values).stream().noneMatch(v -> this.matches(v.getFirst(), v.getLast()));
    }

    /**
     * Returns binary flag based on any-match input collection of {@link Iterable} collection of {@link Entry} values
     *
     * @param values - initial input {@link Iterable} collection of {@link Entry} values
     * @return true - if all input values {@link Entry} match, false - otherwise
     */
    default boolean anyMatch(final Iterable<Entry<T, T>> values) {
        return listOf(values).stream().anyMatch(v -> this.matches(v.getFirst(), v.getLast()));
    }

    /**
     * Appends input {@link MatchDescription} by current description
     *
     * @param description - initial input {@link MatchDescription}
     */
    default void describeBy(final MatchDescription description) {
        description.append("(");
        description.append(this.getDescription());
        description.append(")");
    }

    /**
     * Tests input {@link Supplier} by {@link Matcher}
     *
     * @param <T>           type of input element to be matched by operation
     * @param matcher       - initial input {@link BiMatcher}
     * @param firstSupplier - initial input first {@link Supplier}
     * @param lastSupplier  - initial input last {@link Supplier}
     * @return true - if input {@link Supplier}s matches {@link BiMatcher}, false - otherwise
     * @throws NullPointerException if matcher is {@code null}
     * @throws NullPointerException if firstSupplier are {@code null}
     * @throws NullPointerException if lastSupplier are {@code null}
     */
    @NonNull
    static <T> boolean test(final BiMatcher<T> matcher, final Supplier<T> firstSupplier, final Supplier<T> lastSupplier) {
        Objects.requireNonNull(matcher, "Matcher should not be null!");
        Objects.requireNonNull(firstSupplier, "First supplier should not be null!");
        Objects.requireNonNull(lastSupplier, "Last supplier should not be null!");

        try {
            return matcher.matches(firstSupplier.get(), lastSupplier.get());
        } catch (Throwable t) {
            BiMatchOperationException.throwIncorrectMatch(firstSupplier, lastSupplier, t);
        }
        return false;
    }

    /**
     * Returns {@link BiMatcher} by input {@link Comparator}
     *
     * @param <T>        type of input element to be compared by operation
     * @param comparator - initial input {@link Comparator}
     * @return {@link BiMatcher}
     * @throws NullPointerException if comparator is {@code null}
     */
    @NonNull
    static <T> BiMatcher<T> equalBy(final Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "Comparator should not be null");
        return (final T a, final T b) -> Objects.compare(a, b, comparator) == 0;
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "AND" of {@link BiMatcher}s collection
     *
     * @param matchers - initial input {@link BiMatcher} operators to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if matchers is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0		0
     *                              0		1		0
     *                              1	 	0		0
     *                              1		1		1
     *                              </p>
     */
    @NonNull
    @SuppressWarnings("varargs")
    static <T> BiMatcher<T> andAll(final BiMatcher<T>... matchers) {
        Objects.requireNonNull(matchers, "BiMatchers should not be null!");
        return reduceOrThrow(matchers, (BiMatcher<T> m) -> m.getMode().isEnable(), BiMatcher::and, () -> InvalidParameterException.throwError("Unable to combine matchers = {%s} via logical AND", join(matchers, "|")));
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "OR" of {@link BiMatcher}s collection
     *
     * @param <T>      type of input element to be matched by operation
     * @param matchers - initial input {@link BiMatcher} operators to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if matchers is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0		0
     *                              0 		1		1
     *                              1		0 		1
     *                              1		1		1
     *                              </p>
     */
    @NonNull
    @SuppressWarnings("varargs")
    static <T> BiMatcher<T> orAll(final BiMatcher<T>... matchers) {
        Objects.requireNonNull(matchers, "BiMatchers should not be null!");
        return reduceOrThrow(matchers, (BiMatcher<T> m) -> m.getMode().isEnable(), BiMatcher::or, () -> InvalidParameterException.throwError("Unable to combine matchers = {%s} via logical OR", join(matchers, "|")));
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "XOR" of {@link BiMatcher}s collection
     *
     * @param <T>      type of input element to be matched by operation
     * @param matchers - initial input {@link BiMatcher} operators to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if matchers is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0		0
     *                              0		1		1
     *                              1		0 		1
     *                              1		1		0
     *                              </p>
     */
    @NonNull
    @SuppressWarnings("varargs")
    static <T> BiMatcher<T> xorAll(final BiMatcher<T>... matchers) {
        Objects.requireNonNull(matchers, "BiMatchers should not be null!");
        return reduceOrThrow(matchers, (BiMatcher<T> m) -> m.getMode().isEnable(), BiMatcher::xor, () -> InvalidParameterException.throwError("Unable to combine matchers = {%s} via logical XOR", join(matchers, "|")));
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "NAND" of {@link BiMatcher}s collection
     *
     * @param <T>      type of input element to be matched by operation
     * @param matchers - initial input {@link BiMatcher} operators to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if matchers is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0 		0 		1
     *                              0 		1		1
     *                              1		0 		1
     *                              1		1	 	0
     *                              </p>
     */
    @NonNull
    @SuppressWarnings("varargs")
    static <T> BiMatcher<T> nandAll(final BiMatcher<T>... matchers) {
        Objects.requireNonNull(matchers, "BiMatchers should not be null!");
        return reduceOrThrow(matchers, (BiMatcher<T> m) -> m.getMode().isEnable(), BiMatcher::nand, () -> InvalidParameterException.throwError("Unable to combine matchers = {%s} via logical NAND", join(matchers, "|")));
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "NOR" of {@link BiMatcher}s collection
     *
     * @param <T>      type of input element to be matched by operation
     * @param matchers - initial input {@link BiMatcher} operators to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if matchers is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0 	 	1
     *                              0 		1		0
     *                              1		0		0
     *                              1		1		0
     *                              </p>
     */
    @NonNull
    @SuppressWarnings("varargs")
    static <T> BiMatcher<T> norAll(final BiMatcher<T>... matchers) {
        Objects.requireNonNull(matchers, "BiMatchers should not be null!");
        return reduceOrThrow(matchers, (BiMatcher<T> m) -> m.getMode().isEnable(), BiMatcher::nor, () -> InvalidParameterException.throwError("Unable to combine matchers = {%s} via logical NOR", join(matchers, "|")));
    }

    /**
     * Returns composed {@link BiMatcher} operator that represents a short-circuiting logical "XNOR" of {@link BiMatcher} collection
     *
     * @param <T>      type of input element to be matched by operation
     * @param matchers - initial input {@link BiMatcher} operators to perform operation by
     * @return composed {@link BiMatcher} operator
     * @throws NullPointerException if matchers is {@code null}
     *                              <p>
     *                              Input 1	Input 2	Output
     *                              0		0 	 	1
     *                              0 		1	 	0
     *                              1		0		0
     *                              1		1		1
     *                              </p>
     */
    @NonNull
    @SuppressWarnings("varargs")
    static <T> BiMatcher<T> xnorAll(final BiMatcher<T>... matchers) {
        Objects.requireNonNull(matchers, "BiMatchers should not be null!");
        return reduceOrThrow(matchers, (BiMatcher<T> m) -> m.getMode().isEnable(), BiMatcher::xnor, () -> InvalidParameterException.throwError("Unable to combine matchers = {%s} via logical XNOR", join(matchers, "|")));
    }
}
