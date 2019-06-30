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
package com.wildbeeslabs.sensiblemetrics.diffy.converter.service;

import com.wildbeeslabs.sensiblemetrics.diffy.common.exception.InvalidFormatException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A converter to parse enums
 *
 * @param <T> the enum type
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EnumConverter<T extends Enum<T>> extends AbstractConverter<String, T> {

    private final Class<T> clazz;

    /**
     * Constructs a new converter.
     *
     * @param clazz the enum class
     */
    public EnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T valueOf(final String value) {
        try {
            try {
                return Enum.valueOf(clazz, value);
            } catch (IllegalArgumentException e) {
                return Enum.valueOf(clazz, value.toUpperCase());
            }
        } catch (Exception e) {
            InvalidFormatException.throwInvalidFormat(value, e);
        }
        return null;
    }
}
