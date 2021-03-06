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
package com.wildbeeslabs.sensiblemetrics.diffy.validator.service;

import com.wildbeeslabs.sensiblemetrics.diffy.processor.service.CurrencyProcessor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p><b>Currency Validation</b> and Conversion routines (<code>java.math.BigDecimal</code>).</p>
 *
 * <p>This is one implementation of a currency validator that has the following features:</p>
 * <ul>
 * <li>It is <i>lenient</i> about the the presence of the <i>currency symbol</i></li>
 * <li>It converts the currency to a <code>java.math.BigDecimal</code></li>
 * </ul>
 *
 * <p>However any of the <i>number</i> validators can be used for <i>currency</i> validation.
 * For example, if you wanted a <i>currency</i> validator that converts to a
 * <code>java.lang.Integer</code> then you can simply instantiate an
 * <code>IntegerProcessor</code> with the appropriate <i>format type</i>:</p>
 *
 * <p><code>... = new IntegerProcessor(false, IntegerProcessor.CURRENCY_FORMAT);</code></p>
 *
 * <p>Pick the appropriate validator, depending on the type (e.g Float, Double, Integer, Long etc)
 * you want the currency converted to. One thing to note - only the CurrencyProcessor
 * implements <i>lenient</i> behaviour regarding the currency symbol.</p>
 *
 * @version $Revision: 1739356 $
 * @since Validator 1.3.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CurrencyValidator extends BigDecimalValidator2 {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -1300716919863020893L;

    /**
     * Default {@link CurrencyValidator} instance
     */
    private static final CurrencyValidator VALIDATOR = new CurrencyValidator();

    /**
     * DecimalFormat's currency symbol
     */
    private static final char CURRENCY_SYMBOL = '\u00A4';

    /**
     * Return a singleton instance of this validator.
     *
     * @return A singleton instance of the CurrencyProcessor.
     */
    public static CurrencyValidator getInstance() {
        return VALIDATOR;
    }

    /**
     * Construct a <i>strict</i> instance.
     */
    public CurrencyValidator() {
        this(true, true);
    }

    /**
     * Construct an instance with the specified strict setting.
     *
     * @param strict         <code>true</code> if strict
     *                       <code>Format</code> parsing should be used.
     * @param allowFractions <code>true</code> if fractions are
     *                       allowed or <code>false</code> if integers only.
     */
    public CurrencyValidator(boolean strict, boolean allowFractions) {
        super(new CurrencyProcessor(strict, allowFractions));
    }
}
