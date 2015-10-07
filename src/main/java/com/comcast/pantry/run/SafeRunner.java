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

public class SafeRunner implements Runnable {

    private RunMethodList target;
    private Throwable thrown;
    private boolean started;
    private boolean finished;

    public SafeRunner(RunMethodList target) {
        this.target = target;

        this.thrown = null;
        this.started = false;
        this.finished = false;
    }

    public void run() {
        this.started = true;
        try {
            for (Method method : target) {
                method.invoke(target.getTarget());
            }
        } catch (Throwable thrown) {
            this.thrown = thrown;
        } finally {
            this.finished = true;
        }
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isRunning() {
        return started && !finished;
    }

    public boolean didFail() {
        return (null != thrown);
    }

    public Throwable getThrown() {
        return thrown;
    }
}
