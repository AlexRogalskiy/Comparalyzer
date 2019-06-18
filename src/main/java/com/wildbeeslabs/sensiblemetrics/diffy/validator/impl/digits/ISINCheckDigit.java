package com.wildbeeslabs.sensiblemetrics.diffy.validator.impl.digits;

import com.wildbeeslabs.sensiblemetrics.diffy.exception.InvalidParameterException;
import com.wildbeeslabs.sensiblemetrics.diffy.validator.iface.DigitValidator;

/**
 * Modulus 10 <b>ISIN</b> (International Securities Identifying Number) Check Digit calculation/validation.
 *
 * <p>
 * ISIN Numbers are 12 character alphanumeric codes used
 * to identify Securities.
 * </p>
 *
 * <p>
 * Check digit calculation uses the <i>Modulus 10 Double Add Double</i> technique
 * with every second digit being weighted by 2. Alphabetic characters are
 * converted to numbers by their position in the alphabet starting with A being 10.
 * Weighted numbers greater than ten are treated as two separate numbers.
 * </p>
 *
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/ISIN">Wikipedia - ISIN</a>
 * for more details.
 * </p>
 *
 * @version $Revision: 1739356 $
 * @since Validator 1.4
 */
public final class ISINCheckDigit extends BaseDigitValidator {

    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')

    /**
     * Singleton ISIN Check Digit instance
     */
    public static final DigitValidator ISIN_CHECK_DIGIT = new ISINCheckDigit();

    /**
     * weighting given to digits depending on their right position
     */
    private static final int[] POSITION_WEIGHT = new int[]{2, 1};

    /**
     * Construct an ISIN Indetifier Check Digit routine.
     */
    public ISINCheckDigit() {
        super(10); // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Calculate the modulus for an ISIN code.
     *
     * @param code               The code to calculate the modulus for.
     * @param includesCheckDigit Whether the code includes the Check Digit or not.
     * @return The modulus value
     * @throws InvalidParameterException if an error occurs calculating the modulus
     *                                   for the specified code
     */
    @Override
    protected int calculateModulus(String code, boolean includesCheckDigit) throws InvalidParameterException {
        StringBuilder transformed = new StringBuilder(code.length() * 2);
        if (includesCheckDigit) {
            char checkDigit = code.charAt(code.length() - 1); // fetch the last character
            if (!Character.isDigit(checkDigit)) {
                throw new InvalidParameterException("Invalid checkdigit[" + checkDigit + "] in " + code);
            }
        }
        for (int i = 0; i < code.length(); i++) {
            int charValue = Character.getNumericValue(code.charAt(i));
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new InvalidParameterException("Invalid Character[" + (i + 1) + "] = '" + charValue + "'");
            }
            // this converts alphanumerics to two digits
            // so there is no need to overload toInt()
            transformed.append(charValue);
        }
        return super.calculateModulus(transformed.toString(), includesCheckDigit);
    }

    /**
     * <p>Calculates the <i>weighted</i> value of a charcter in the
     * code at a specified position.</p>
     *
     * <p>For Luhn (from right to left) <b>odd</b> digits are weighted
     * with a factor of <b>one</b> and <b>even</b> digits with a factor
     * of <b>two</b>. Weighted values &gt; 9, have 9 subtracted</p>
     *
     * @param charValue The numeric value of the character.
     * @param leftPos   The position of the character in the code, counting from left to right
     * @param rightPos  The positionof the character in the code, counting from right to left
     * @return The weighted value of the character.
     */
    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        int weightedValue = (charValue * weight);
        return sumDigits(weightedValue);
    }
}