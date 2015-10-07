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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RunMethodList implements Iterable<Method> {

    private Object target;
    private List<Method> methods = new ArrayList<Method>();

    public RunMethodList(Object target) {
        this.target = target;
    }

    public synchronized void add(Method method) throws UnsupportedRunMethodException {
        if (method.getParameterTypes().length != 0) {
            throw new UnsupportedRunMethodException("Cannot invoke methods with arguments: "
                    + method.toString());
        } else if (!Modifier.isStatic(method.getModifiers()) && target == null) {
            throw new UnsupportedRunMethodException(
                    "Cannot invoke static method without a target object: " + method.toString());
        }

        methods.add(method);
        Collections.sort(methods, new MethodComparator());
    }

    public synchronized Iterator<Method> iterator() {
        return Collections.unmodifiableList(methods).iterator();
    }

    public Object getTarget() {
        return target;
    }

    public SafeRunner toRunner() {
        return new SafeRunner(this);
    }

    public Thread toThread() {
        return new Thread(toRunner());
    }

    private class MethodComparator implements Comparator<Method> {

        public int compare(Method m1, Method m2) {
            int o1 = 0;
            int o2 = 0;

            if (m1.isAnnotationPresent(Run.class)) {
                o1 = m1.getAnnotation(Run.class).order();
            }
            if (m2.isAnnotationPresent(Run.class)) {
                o2 = m2.getAnnotation(Run.class).order();
            }

            if (o1 < o2) {
                return -1;
            } else if (o1 > o2) {
                return 1;
            } else {
                return m1.getName().compareTo(m2.getName());
            }
        }
    }
}
