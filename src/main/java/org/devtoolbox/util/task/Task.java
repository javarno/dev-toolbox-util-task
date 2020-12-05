/*
 * MIT License
 *
 * Copyright © 2020 dev-toolbox.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.devtoolbox.util.task;

import org.devtoolbox.util.task.listener.TaskCompletionListener;
import org.devtoolbox.util.task.listener.TaskListener;
import org.devtoolbox.util.task.status.TaskStatus;


/**
 * Generic task interface.
 *
 * @author Arnaud Lecollaire
 */
public interface Task {

    /**
     * Starts the task.
     */
    void perform();

    /**
     * Registers a listener interested in task events.
     *
     * @throws NullPointerException if listener is null
     */
    void addTaskListener(TaskListener listener);

    /**
     * Removes a listener that was previously registered to receive task events.
     *
     * @throws NullPointerException if listener is null
     * @throws IllegalArgumentException if listener has not been registered
     */
    void removeTaskListener(TaskListener listener);

    /**
     * Registers a listener interested in task completion events.
     *
     * @param listener
     */
    void addTaskCompletionListener(final TaskCompletionListener listener);

    /**
     * Removes a listener that was previously registered to receive task completion events.
     *
     * @throws NullPointerException if listener is null
     * @throws IllegalArgumentException if listener has not been registered
     */
    public void removeTaskCompletionListener(final TaskCompletionListener listener);

    TaskStatus getStatus();

}