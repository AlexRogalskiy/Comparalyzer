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
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.listener.impl;

import com.wildbeeslabs.sensiblemetrics.diffy.matcher.event.BaseMatcherEvent;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.event.MatcherEvent;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.iface.EventListener;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.listener.iface.MatcherEventListener;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Logging {@link MatcherEventListener} implementation
 *
 * @param <T> type of input element to be matched by operation
 */
@Slf4j
@EqualsAndHashCode
@ToString
public class LoggingMatcherEventListener<T> implements MatcherEventListener<T> {

    /**
     * Logging {@link MatcherEventListener} instance
     */
    public static final MatcherEventListener INSTANCE = new LoggingMatcherEventListener<>();

    /**
     * {@link MatcherEventListener} on start {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onStart(final E event) {
        log.info("{}, on start event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * {@link MatcherEventListener} on before {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onBefore(final E event) {
        log.info("{}, on before event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * {@link MatcherEventListener} on after {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onAfter(final E event) {
        log.info("{}, on after event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * {@link MatcherEventListener} on success {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onSuccess(final E event) {
        log.info("{}, on success event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * {@link MatcherEventListener} on error {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onError(final E event) {
        log.info("{}, on error event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * {@link MatcherEventListener} on complete {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onComplete(final E event) {
        log.info("{}, on complete event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * {@link MatcherEventListener} on skip {@link MatcherEvent}
     *
     * @param event - initial input {@link MatcherEvent}
     */
    @Override
    public <E extends BaseMatcherEvent<T>> void onSkip(final E event) {
        log.info("{}, on skip event: {}, description: {}", this.getClass().getName(), event, event.getMatcher().getDescription());
    }

    /**
     * Returns {@link List} of supported {@link EventListener}s
     *
     * @return {@link List} of supported {@link EventListener}s
     */
    @Override
    public List<? extends EventListener<T>> getSupportedListeners() {
        return asList((EventListener<T>) INSTANCE);
    }
}
