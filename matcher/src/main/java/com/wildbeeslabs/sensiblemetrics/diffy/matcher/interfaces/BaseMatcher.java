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
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.interfaces;

import com.wildbeeslabs.sensiblemetrics.diffy.matcher.description.iface.MatchDescription;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.enumeration.BiMatcherModeType;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.listener.iface.MatcherEventListener;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Base matcher interface declaration
 *
 * @param <T> type of input element to be matched by operation
 * @param <S> type of input element to be collected by match by operation
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
public interface BaseMatcher<T, S> extends Serializable {

    /**
     * Returns {@link List} of {@link MatcherEventListener}
     *
     * @return {@link List} of {@link MatcherEventListener}
     */
    @NonNull
    default <E extends MatcherEventListener<T, S>> List<E> getListeners() {
        return Collections.emptyList();
    }

    /**
     * Returns {@link BiMatcherModeType}
     *
     * @return {@link BiMatcherModeType}
     */
    @NonNull
    default BaseMatcherMode getMode() {
        return BaseMatcherMode.DEFAULT_MATCHER_MODE;
    }

    /**
     * Returns {@link MatchDescription}
     *
     * @return {@link MatchDescription}
     */
    @NonNull
    default MatchDescription getDescription() {
        return MatchDescription.EMPTY_MATCH_DESCRIPTION;
    }

    /**
     * Returns binary flag by input {@link BaseMatcher} mode status
     *
     * @return true - if {@link BaseMatcher} is enabled, false - otherwise
     */
    default boolean isEnable() {
        return this.getMode().isEnable();
    }
}
