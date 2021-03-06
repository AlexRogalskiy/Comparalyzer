package com.wildbeeslabs.sensiblemetrics.diffy.generator.service.datetime;

import com.wildbeeslabs.sensiblemetrics.diffy.generator.interfaces.GenerationStatus;
import com.wildbeeslabs.sensiblemetrics.diffy.generator.interfaces.InRange;
import com.wildbeeslabs.sensiblemetrics.diffy.generator.service.Generator;
import com.wildbeeslabs.sensiblemetrics.diffy.generator.service.SourceOfRandomness;

import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.wildbeeslabs.sensiblemetrics.diffy.generator.utils.Reflection.defaultValueOf;

/**
 * Produces values of type {@link ZonedDateTime}.
 */
public class ZonedDateTimeGenerator extends Generator<ZonedDateTime> {
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    private ZonedDateTime min =
        ZonedDateTime.of(Year.MIN_VALUE, 1, 1, 0, 0, 0, 0, UTC_ZONE_ID);
    private ZonedDateTime max =
        ZonedDateTime.of(Year.MAX_VALUE, 12, 31, 23, 59, 59, 999_999_999, UTC_ZONE_ID);

    public ZonedDateTimeGenerator() {
        super(ZonedDateTime.class);
    }

    /**
     * <p>Tells this generator to produce values within a specified
     * {@linkplain InRange#min() minimum} and/or {@linkplain InRange#max()
     * maximum}, inclusive, with uniform distribution, down to the
     * nanosecond.</p>
     *
     * <p>If an endpoint of the range is not specified, the generator will use
     * dates with values of either {@link java.time.Instant#MIN} or
     * {@link java.time.Instant#MAX} and UTC zone as appropriate.</p>
     *
     * <p>{@link InRange#format()} describes
     * {@linkplain DateTimeFormatter#ofPattern(String) how the generator is to
     * interpret the range's endpoints}.</p>
     *
     * @param range annotation that gives the range's constraints
     */
    public void configure(InRange range) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(range.format());
        if (!defaultValueOf(InRange.class, "min").equals(range.min())) {
            min = ZonedDateTime.parse(range.min(), formatter)
                .withZoneSameInstant(UTC_ZONE_ID);
        }
        if (!defaultValueOf(InRange.class, "max").equals(range.max())) {
            max = ZonedDateTime.parse(range.max(), formatter)
                .withZoneSameInstant(UTC_ZONE_ID);
        }

        if (min.compareTo(max) > 0)
            throw new IllegalArgumentException(String.format("bad range, %s > %s", range.min(), range.max()));
    }

    @Override
    public ZonedDateTime generate(SourceOfRandomness random, GenerationStatus status) {
        // Project the ZonedDateTime to an Instant for easy long-based generation.
        return ZonedDateTime.ofInstant(
            random.nextInstant(min.toInstant(), max.toInstant()),
            UTC_ZONE_ID);
    }
}
