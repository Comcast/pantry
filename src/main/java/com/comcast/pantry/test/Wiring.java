/**
 * Copyright 2015 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comcast.pantry.test;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test Utility class for wiring dependencies into classes under test without having to write entire
 * spring application contexts. In some cases, this is the only good way to inject dependencies.
 *
 * @author Clark Malmgren
 */
public class Wiring<T> {

    private T target;
    private Class<T> clazz;

    /**
     * Wire the given object <code>s</code> to the field with the given <code>name</code> on the
     * given <code>target</code>. This method will return a newly created Wiring that can be reused
     * to allow for easy call chaining.
     *
     * @param target
     *            the target to wire the object to
     * @param name
     *            the name of the field
     * @param s
     *            the object to set that field to
     *
     * @return the newly created {@link Wiring} object for the given <code>target</code>
     */
    public static <S, T> Wiring<T> wire(T target, String name, S s) {
        Wiring<T> wiring = new Wiring<T>(target);
        return wiring.wire(name, s);
    }

    /**
     * Autowire the given object <code>s</code> to the first field found with an
     * <code>@Autowired</code> annotation that is assignable with the given object on the given
     * <code>target</code>. This method will return a newly created Wiring that can be reused to
     * allow for easy call chaining..
     *
     * @param target
     *            the target to wire the object to
     * @param s
     *            the object to autowire into the target object
     *
     * @return the newly created {@link Wiring} object for the given <code>target</code>
     */
    public static <S, T> Wiring<T> autowire(T target, S s) {
        Wiring<T> wiring = new Wiring<T>(target);
        return wiring.autowire(s);
    }

    /**
     * Construct a new Wiring for a given object.
     *
     * @param target
     *            the object that will be wired
     */
    @SuppressWarnings("unchecked")
    public Wiring(T target) {
        this.target = target;
        this.clazz = (Class<T>) target.getClass();
    }

    /**
     * Wire the given object <code>s</code> to the field with the given <code>name</code>. This
     * method will return <code>this</code> to allow for easy call chaining.
     *
     * @param name
     *            the name of the field
     * @param s
     *            the object to set that field to
     *
     * @return <code>this</code>
     */
    public <S> Wiring<T> wire(String name, S s) {
        try {
            Field field = findField(clazz, name);
            field.setAccessible(true);
            field.set(target, s);
            return this;
        } catch (Exception ex) {
            throw new WiringException(ex);
        }
    }

    /**
     * Autowire the given object <code>s</code> to the first field found with an
     * <code>@Autowired</code> annotation that is assignable with the given object. This method will
     * return <code>this</code> to allow for easy call chaining.
     *
     * @param s
     *            the object to autowire into the target object
     *
     * @return <code>this</code>
     */
    public <S> Wiring<T> autowire(S s) {
        try {
            try {
                String name = s.getClass().getSimpleName();
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                Field field = clazz.getField(name);
                field.setAccessible(true);
                field.set(target, s);
            } catch (NoSuchFieldException nsfex) {
                // it was worth a shot...
            }

            Field field = findField(clazz, s.getClass());
            field.setAccessible(true);
            field.set(target, s);
            return this;
        } catch (Exception ex) {
            throw new WiringException(ex);
        }
    }

    /**
     * Recursive helper method to find a field with the given name.
     *
     * @param clazz
     *            the current class to search
     * @param name
     *            the type of the field
     *
     * @return an applicable field
     */
    private Field findField(Class<?> clazz, String name) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        Class<?> supar = clazz.getSuperclass();
        if (null != supar && !Object.class.equals(supar)) {
            return findField(supar, name);
        } else {
            throw new WiringException("Couldn't find an field named: " + name);
        }
    }

    /**
     * Recursive helper method to find a field that is assignable from the given type.
     *
     * @param clazz
     *            the current class to search
     * @param type
     *            the type of the object that is trying to be autowired into the target class
     *
     * @return an applicable field
     */
    private Field findField(Class<?> clazz, Class<?> type) {
        for (Field field : clazz.getDeclaredFields()) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (null != autowired && field.getType().isAssignableFrom(type)) {
                return field;
            }
        }

        Class<?> supar = clazz.getSuperclass();
        if (null != supar && !Object.class.equals(supar)) {
            return findField(supar, type);
        } else {
            throw new WiringException("Couldn't find an @Autowired field for the given type: "
                    + type.getName());
        }
    }
}
