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
package com.wildbeeslabs.sensiblemetrics.diffy.formatter.exception;

import com.wildbeeslabs.sensiblemetrics.diffy.common.annotation.Factory;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.wildbeeslabs.sensiblemetrics.diffy.common.utils.StringUtils.formatMessage;

/**
 * Format {@link RuntimeException} implementation
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FormatException extends RuntimeException {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -7335978919645418617L;

    /**
     * Format exception constructor with initial input message
     *
     * @param message - initial input message {@link String}
     */
    public FormatException(final String message) {
        super(message);
    }

    /**
     * Format exception constructor with initial input {@link Throwable}
     *
     * @param cause - initial input cause target {@link Throwable}
     */
    public FormatException(final Throwable cause) {
        super(cause);
    }

    /**
     * Format exception constructor with initial input message and {@link Throwable}
     *
     * @param message - initial input message {@link String}
     * @param cause   - initial input cause target {@link Throwable}
     */
    public FormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns {@link FormatException} by input parameters
     *
     * @param target - initial input target {@link Object}
     * @return {@link FormatException}
     */
    @Factory
    public static final FormatException throwReject(final Object target) {
        throw new FormatException(formatMessage("ERROR: cannot format target: {%s}", target));
    }
}
