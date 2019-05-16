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
package com.wildbeeslabs.sensiblemetrics.diffy.exception;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.wildbeeslabs.sensiblemetrics.diffy.utils.StringUtils.formatMessage;

/**
 * Method invocation {@link RuntimeException} implementation
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MethodInvocationException extends RuntimeException {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -6770900397605904118L;

    /**
     * Method invocation exception constructor with initial input message
     *
     * @param message - initial input message {@link String}
     */
    public MethodInvocationException(final String message) {
        super(message);
    }

    /**
     * Method invocation exception constructor with initial input {@link Throwable}
     *
     * @param cause - initial input {@link Throwable}
     */
    public MethodInvocationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Method invocation exception constructor with initial input message and {@link Throwable}
     *
     * @param message - initial input message {@link String}
     * @param cause   - initial input {@link Throwable}
     */
    public MethodInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns {@link MethodInvocationException} instance by input parameters
     *
     * @param methodName - initial input property name {@link String}
     * @param target     - initial input source target {@link Object}
     * @param throwable  - initial input cause instance {@link Throwable}
     * @return {@link MethodInvocationException}
     */
    public static final MethodInvocationException throwMethodInvocation(final String methodName, final Object target, final Throwable throwable) {
        throw new MethodInvocationException(formatMessage("ERROR: cannot invoke method: {%s} on target: {%s}", methodName, target), throwable);
    }
}
