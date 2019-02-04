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
package com.wildbeeslabs.sensiblemetrics.comparalyzer.matcher.impl;

import com.wildbeeslabs.sensiblemetrics.comparalyzer.entity.DeliveryInfo;
import com.wildbeeslabs.sensiblemetrics.comparalyzer.matcher.Matcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

/**
 * Custom matcher implementation for delivery information instance {@link DeliveryInfo}
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeliveryInfoMatcher extends AbstractMatcher<DeliveryInfo> {

    /**
     * Determines delivery info "Type" field matcher {@link Matcher}
     */
    public static Function<Integer, Matcher<? super DeliveryInfo>> DELIVERY_TYPE_MATCHER = type -> new AbstractTypeSafeMatcher<DeliveryInfo>() {
        @Override
        public boolean matchesSafe(final DeliveryInfo value) {
            return Objects.equals(type, value.getType());
        }
    };

    /**
     * Determines delivery info "Gid" field matcher {@link Matcher}
     */
    public static Function<String, Matcher<? super DeliveryInfo>> DELIVERY_GID_MATCHER = gid -> new AbstractTypeSafeMatcher<DeliveryInfo>() {
        @Override
        public boolean matchesSafe(final DeliveryInfo value) {
            return Objects.equals(gid, value.getGid());
        }
    };

    /**
     * Determines delivery info "Created date" matcher {@link Matcher}
     */
    public static Function<Date, Matcher<? super DeliveryInfo>> DELIVERY_CREATED_DATE_MATCHER = date -> new AbstractTypeSafeMatcher<DeliveryInfo>() {
        @Override
        public boolean matchesSafe(final DeliveryInfo value) {
            return Objects.equals(date, value.getCreatedAt());
        }
    };

    /**
     * Determines delivery info "Updated date" matcher {@link Matcher}
     */
    public static Function<Date, Matcher<? super DeliveryInfo>> DELIVERY_UPDATED_DATE_MATCHER = date -> new AbstractTypeSafeMatcher<DeliveryInfo>() {
        @Override
        public boolean matchesSafe(final DeliveryInfo value) {
            return Objects.equals(date, value.getUpdatedAt());
        }
    };

    /**
     * Determines delivery info "Type" field matcher {@link Matcher}
     */
    public static Function<Matcher<? super Integer>, Matcher<? super DeliveryInfo>> FIELD_TYPE_MATCHER = matcher -> new AbstractFieldMatcher<DeliveryInfo, Integer>(matcher) {
        @Override
        protected Integer valueOf(final DeliveryInfo value) {
            return value.getType();
        }
    };

    @Override
    public boolean matches(final DeliveryInfo value) {
        return getMatchers().stream().allMatch(matcher -> matcher.matches(value));
    }

    private DeliveryInfoMatcher() {
        addMatcher(InstanceMatcher.getMatcher(DeliveryInfo.class));
    }

    public DeliveryInfoMatcher withType(final Integer type) {
        getMatchers().add(DELIVERY_TYPE_MATCHER.apply(type));
        return this;
    }

    public DeliveryInfoMatcher withGid(final String gid) {
        getMatchers().add(DELIVERY_GID_MATCHER.apply(gid));
        return this;
    }

    public DeliveryInfoMatcher withCreatedDate(final Date createdDate) {
        getMatchers().add(DELIVERY_CREATED_DATE_MATCHER.apply(createdDate));
        return this;
    }

    public DeliveryInfoMatcher withUpdatedDate(final Date updatedDate) {
        getMatchers().add(DELIVERY_UPDATED_DATE_MATCHER.apply(updatedDate));
        return this;
    }

    public DeliveryInfoMatcher withType(final Matcher<? super Integer> matcher) {
        getMatchers().add(FIELD_TYPE_MATCHER.apply(matcher));
        return this;
    }

    public static DeliveryInfoMatcher getMatcher() {
        return new DeliveryInfoMatcher();
    }
}