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
package com.wildbeeslabs.sensiblemetrics.diffy.common.exception;

import com.wildbeeslabs.sensiblemetrics.diffy.common.annotation.Factory;
import com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Unsupported scheme type {@link RuntimeException} implementation
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UnsupportedSchemeTypeException extends RuntimeException {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -6417441171307580144L;

    /**
     * Unsupported scheme type exception constructor with initial input message
     *
     * @param message - initial input message {@link String}
     */
    public UnsupportedSchemeTypeException(final String message) {
        super(message);
    }

    /**
     * Unsupported scheme type exception constructor with initial input {@link Throwable}
     *
     * @param cause - initial input cause target {@link Throwable}
     */
    public UnsupportedSchemeTypeException(final Throwable cause) {
        super(cause);
    }

    /**
     * Unsupported scheme type exception constructor with initial input message and {@link Throwable}
     *
     * @param message - initial input source message {@link String}
     * @param cause   - initial input cause target {@link Throwable}
     */
    public UnsupportedSchemeTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns {@link UnsupportedSchemeTypeException} by input parameters
     *
     * @param target - initial input source target {@link Object}
     * @return {@link UnsupportedSchemeTypeException}
     */
    @Factory
    public static final UnsupportedSchemeTypeException throwUnsupportedSchemeType(final Object target) {
        throw new UnsupportedSchemeTypeException(StringUtils.formatMessage("ERROR: cannot process scheme type by input code = {%s}", target));
    }

    /**
     * Returns {@link UnsupportedSchemeTypeException} by input parameters
     *
     * @param message   - initial input raw message {@link String}
     * @param throwable - initial input cause target {@link Throwable}
     * @return {@link UnsupportedSchemeTypeException}
     */
    @Factory
    public static final UnsupportedSchemeTypeException throwError(final String message, final Throwable throwable) {
        throw new UnsupportedSchemeTypeException(message, throwable);
    }
}
