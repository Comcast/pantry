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

package com.comcast.pantry.run;

import java.lang.reflect.Method;

public class RunFactory {

    public RunMethodList scan(Object target, String... names) throws UnsupportedRunMethodException {
        Class<?> clazz = null;
        RunMethodList list = null;
        if (target instanceof Class) {
            clazz = (Class<?>) target;
            list = new RunMethodList(null);
        } else {
            clazz = target.getClass();
            list = new RunMethodList(target);
        }

        scanRecursive(list, clazz, names);

        return list;
    }

    private void scanRecursive(RunMethodList list, Class clazz, String[] names) throws UnsupportedRunMethodException {
        /* First scan on this class */
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Run.class) && accept(m, names)) {
                list.add(m);
            }
        }

        /* Then scan on each of the interfaces */
        for (Class iface : clazz.getInterfaces()) {
            scanRecursive(list, iface, names);
        }

        /* Finally, scan the super class (if it is not Object.class) */
        Class supar = clazz.getSuperclass();
        if (null != supar && Object.class.equals(supar)) {
            scanRecursive(list, supar, names);
        }
    }

    private boolean accept(Method method, String[] names) {
        if (names == null || names.length == 0) {
            return true;
        }

        Run run = method.getAnnotation(Run.class);
        if (run.name().equals("")) {
            return true;
        } else {
            for (String name : names) {
                if (run.name().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }
}
