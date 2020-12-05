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
package org.devtoolbox.util.task.implementation;

import org.devtoolbox.util.task.error.TaskErrorType;
import org.devtoolbox.util.task.error.TaskException;
import org.devtoolbox.util.task.status.TaskEndStatus;
import org.devtoolbox.util.task.status.TaskStatus;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableValue;


/**
 * @author Arnaud Lecollaire
 */
public abstract class AsynchronousTask extends SynchronousTask {

    private final ReadOnlyBooleanWrapper configurationValidProperty = new ReadOnlyBooleanWrapper(true);
    private final Long timeout = null;


    public AsynchronousTask(final String name) {
        super(name);
    }

    @Override
    protected void startTask() {
        new Thread() {
            @Override
            public void run() {
                AsynchronousTask.super.startTask();
            }
        }.start();
        if (timeout != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(timeout);
                } catch (final InterruptedException error) {
                    sendError(TaskErrorType.ERROR_IN_TIMEOUT_MONITOR, error, getName());
                    return;
                }
                if (getStatus() == TaskStatus.STARTED) {
                    setStatus(TaskStatus.TIMEOUT);
                    setStopAsked(true);
                }
            }).start();
        }
    }

    @Override
    public void sendTaskMessage(final String message, final Object...parameters) {
        doInResultThread(() -> super.sendTaskMessage(message, parameters));
    }

    @Override
    protected void sendTaskStatusEvent(final TaskStatus oldStatus, final TaskStatus newStatus) {
        doInResultThread(() -> super.sendTaskStatusEvent(oldStatus, newStatus));
    }

    @Override
    protected void sendTaskCompletionEvent(final TaskEndStatus executionStatus) {
        doInResultThread(() -> super.sendTaskCompletionEvent(executionStatus));
    }

    @Override
    protected void sendTaskException(final TaskException exception) {
        doInResultThread(() -> super.sendTaskException(exception));
    }

    protected void doInResultThread(final Runnable runnable) {
        if (isInResultThread()) {
            runnable.run();
        } else {
            sendToResultThread(runnable);
        }
    }

    protected boolean isInResultThread() {
        return true;
    }

    protected void sendToResultThread(final Runnable runnable) {
        runnable.run();
    }

    public ReadOnlyBooleanProperty configurationValidProperty() {
        return configurationValidProperty.getReadOnlyProperty();
    }

    protected void bindConfigurationValidTo(final ObservableValue<? extends Boolean> value) {
        configurationValidProperty.bind(value);
    }

    public boolean isConfigurationValid() {
        return configurationValidProperty.get();
    }

    public void setConfigurationValid(final boolean configurationValid) {
        configurationValidProperty.set(configurationValid);
    }
}