/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wildbeeslabs.sensiblemetrics.diffy.matcher.service;

import com.wildbeeslabs.sensiblemetrics.diffy.common.utils.ValidationUtils;
import com.wildbeeslabs.sensiblemetrics.diffy.matcher.interfaces.Matcher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * <p><code>Predicate</code> that evaluates a property value against a specified value.</p>
 * <p>
 * An implementation of <code>org.apache.commons.collections.Predicate</code> that evaluates a
 * property value on the object provided against a specified value and returns <code>true</code>
 * if equal; <code>false</code> otherwise.
 * The <code>BeanPropertyEqualsMatcher</code> constructor takes two parameters which
 * determine what property will be evaluated on the target object and what its expected value should
 * be.
 * <dl>
 * <dt>
 * <strong><code>
 * <pre>public BeanPropertyEqualsMatcher( String propertyName, Object propertyValue )</pre>
 * </code></strong>
 * </dt>
 * <dd>
 * Will create a <code>Predicate</code> that will evaluate the target object and return
 * <code>true</code> if the property specified by <code>propertyName</code> has a value which
 * is equal to the the value specified by <code>propertyValue</code>. Or return
 * <code>false</code> otherwise.
 * </dd>
 * </dl>
 * </p>
 * <p>
 * <strong>Note:</strong> Property names can be a simple, nested, indexed, or mapped property as defined by
 * <code>org.apache.commons.beanutils.PropertyUtils</code>.  If any object in the property path
 * specified by <code>propertyName</code> is <code>null</code> then the outcome is based on the
 * value of the <code>ignoreNull</code> attribute.
 * </p>
 * <p>
 * A typical usage might look like:
 * <code><pre>
 * // create the closure
 * BeanPropertyEqualsMatcher predicate =
 *    new BeanPropertyEqualsMatcher( "activeEmployee", Boolean.FALSE );
 *
 * // filter the Collection
 * CollectionUtils.filter( peopleCollection, predicate );
 * </pre></code>
 * </p>
 * <p>
 * This would take a <code>Collection</code> of person objects and filter out any people whose
 * <code>activeEmployee</code> property is <code>false</code>. Assuming...
 * <ul>
 * <li>
 * The top level object in the <code>peeopleCollection</code> is an object which represents a
 * person.
 * </li>
 * <li>
 * The person object has a <code>getActiveEmployee()</code> method which returns
 * the boolean value for the object's <code>activeEmployee</code> property.
 * </li>
 * </ul>
 * </p>
 * <p>
 * Another typical usage might look like:
 * <code><pre>
 * // create the closure
 * BeanPropertyEqualsMatcher predicate =
 *    new BeanPropertyEqualsMatcher( "personId", "456-12-1234" );
 *
 * // search the Collection
 * CollectionUtils.find( peopleCollection, predicate );
 * </pre></code>
 * </p>
 * <p>
 * This would search a <code>Collection</code> of person objects and return the first object whose
 * <code>personId</code> property value equals <code>456-12-1234</code>. Assuming...
 * <ul>
 * <li>
 * The top level object in the <code>peeopleCollection</code> is an object which represents a
 * person.
 * </li>
 * <li>
 * The person object has a <code>getPersonId()</code> method which returns
 * the value for the object's <code>personId</code> property.
 * </li>
 * </ul>
 * </p>
 *
 * @version $Id: BeanPropertyEqualsMatcher.java 1540509 2013-11-10 18:39:11Z oheger $
 * @see PropertyUtils
 * @see Predicate
 */
@Slf4j
@Data
@EqualsAndHashCode
@ToString
public class BeanPropertyEqualsMatcher<T> implements Matcher<T> {

    /**
     * The name of the property which will be evaluated when this <code>Predicate</code> is executed.
     */
    private final String propertyName;

    /**
     * The value that the property specified by <code>propertyName</code>
     * will be compared to when this <code>Predicate</code> executes.
     */
    private final T propertyValue;

    /**
     * <p>Should <code>null</code> objects in the property path be ignored?</p>
     * <p>
     * Determines whether <code>null</code> objects in the property path will genenerate an
     * <code>IllegalArgumentException</code> or not. If set to <code>true</code> then if any objects
     * in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged but
     * not rethrown and <code>false</code> will be returned.  If set to <code>false</code> then if
     * any objects in the property path evaluate to <code>null</code> then the
     * <code>IllegalArgumentException</code> throw by <code>PropertyUtils</code> will be logged and
     * rethrown.
     * </p>
     */
    private final boolean ignoreNull;

    /**
     * Constructor which takes the name of the property, its expected value to be used in evaluation,
     * and assumes <code>ignoreNull</code> to be <code>false</code>.
     *
     * @param propertyName  The name of the property that will be evaluated against the expected value.
     * @param propertyValue The value to use in object evaluation.
     * @throws IllegalArgumentException If the property name provided is null or empty.
     */
    public BeanPropertyEqualsMatcher(final String propertyName, final T propertyValue) {
        this(propertyName, propertyValue, false);
    }

    /**
     * Constructor which takes the name of the property, its expected value
     * to be used in evaluation, and a boolean which determines whether <code>null</code> objects in
     * the property path will genenerate an <code>IllegalArgumentException</code> or not.
     *
     * @param propertyName  The name of the property that will be evaluated against the expected value.
     * @param propertyValue The value to use in object evaluation.
     * @param ignoreNull    Determines whether <code>null</code> objects in the property path will
     *                      genenerate an <code>IllegalArgumentException</code> or not.
     * @throws IllegalArgumentException If the property name provided is null or empty.
     */
    public BeanPropertyEqualsMatcher(final String propertyName, final T propertyValue, final boolean ignoreNull) {
        ValidationUtils.isTrue(StringUtils.isNotBlank(propertyName));
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.ignoreNull = ignoreNull;
    }

    /**
     * Evaulates the object provided against the criteria specified when this
     * <code>BeanPropertyEqualsMatcher</code> was constructed.  Equality is based on
     * either reference or logical equality as defined by the property object's equals method. If
     * any object in the property path leading up to the target property is <code>null</code> then
     * the outcome will be based on the value of the <code>ignoreNull</code> attribute. By default,
     * <code>ignoreNull</code> is <code>false</code> and would result in an
     * <code>IllegalArgumentException</code> if an object in the property path leading up to the
     * target property is <code>null</code>.
     *
     * @param object The object to be evaluated.
     * @return True if the object provided meets all the criteria for this <code>Predicate</code>;
     * false otherwise.
     * @throws IllegalArgumentException If an IllegalAccessException, InvocationTargetException, or
     *                                  NoSuchMethodException is thrown when trying to access the property specified on the object
     *                                  provided. Or if an object in the property path provided is <code>null</code> and
     *                                  <code>ignoreNull</code> is set to <code>false</code>.
     */
    @Override
    public boolean matches(final T object) {
        try {
            return this.evaluateValue(this.propertyValue, (T) PropertyUtils.getProperty(object, this.propertyName));
        } catch (IllegalArgumentException e) {
            if (!this.ignoreNull) {
                final IllegalArgumentException iae = new IllegalArgumentException(String.format("ERROR: problem during evaluation, null value encountered in property = {%s}", this.propertyName), e);
                if (!BeanUtils.initCause(iae, e)) {
                    log.error(iae.getMessage(), e);
                }
                throw iae;
            }
        } catch (IllegalAccessException e) {
            final IllegalArgumentException iae = new IllegalArgumentException(String.format("ERROR: unable to access the property provided = {%s}", this.propertyName), e);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(iae.getMessage(), e);
            }
            throw iae;
        } catch (InvocationTargetException e) {
            final IllegalArgumentException iae = new IllegalArgumentException(String.format("ERROR: exception occurred in property's getter = {%s}", this.propertyName), e);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(iae.getMessage(), e);
            }
            throw iae;
        } catch (NoSuchMethodException e) {
            final IllegalArgumentException iae = new IllegalArgumentException(String.format("ERROR: property not found = {%s}", this.propertyName), e);
            if (!BeanUtils.initCause(iae, e)) {
                log.error(iae.getMessage(), e);
            }
            throw iae;
        }
        return false;
    }

    /**
     * Utility method which evaluates whether the actual property value equals the expected property
     * value.
     *
     * @param expected The expected value.
     * @param actual   The actual value.
     * @return True if they are equal; false otherwise.
     */
    protected boolean evaluateValue(final T expected, final T actual) {
        return Objects.equals(expected, actual);
    }
}
