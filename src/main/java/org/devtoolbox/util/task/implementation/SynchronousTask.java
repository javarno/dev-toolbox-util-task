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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

import org.devtoolbox.util.task.Task;
import org.devtoolbox.util.task.error.TaskErrorType;
import org.devtoolbox.util.task.error.TaskException;
import org.devtoolbox.util.task.listener.TaskCompletionListener;
import org.devtoolbox.util.task.listener.TaskListener;
import org.devtoolbox.util.task.status.TaskEndStatus;
import org.devtoolbox.util.task.status.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;


/**
 * @author Arnaud Lecollaire
 */
public abstract class SynchronousTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronousTask.class);

    private final Collection<TaskListener> taskListeners = new ArrayList<>();
    private final Collection<TaskCompletionListener> completionListeners = new ArrayList<>();

    private final String name;
    private TaskStatus status = TaskStatus.CREATED;
    private boolean executionFailed = false;
    private boolean stopAsked = false;


    public SynchronousTask(final String name) {
        super();
        this.name = name;
        initializeTask();
        setStatus(TaskStatus.INITIALIZED);
    }

    protected void initializeTask() {}

    public String getName() {
        return name;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    protected boolean isStopAsked() {
        return stopAsked;
    }

    public void setStopAsked(final boolean stopAsked) {
        this.stopAsked = stopAsked;
    }

    public void setStatus(final TaskStatus newStatus) {
        final TaskStatus oldStatus = status;
        if (oldStatus == newStatus) {
            return;
        }
        LOGGER.info("Changing status for task [{}] from [{}] to [{}].", name, oldStatus, newStatus);
        status = newStatus;
        sendStatusChange(oldStatus, newStatus);
    }

    protected void sendStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
        Objects.requireNonNull(oldStatus);
        Objects.requireNonNull(newStatus);
        final int listenersCount = taskListeners.size();
        if (listenersCount == 0) {
            return;
        }
        LOGGER.info("Task [{}] sending status change event from [{}] to [{}] to [{}] listeners ...", name, oldStatus, newStatus, listenersCount);
        sendTaskStatusEvent(oldStatus, newStatus);
    }

    protected void sendTaskStatusEvent(final TaskStatus oldStatus, final TaskStatus newStatus) {
        notifyTaskListeners(listener -> {
            try {
                listener.handleTaskStatusChange(oldStatus, newStatus);
            } catch (final RuntimeException error) {
                sendError(TaskErrorType.LISTENER_NOTIFICATION_FAILED, error, name);
            }
        });
    }

    public void sendTaskMessage(final String message, final Object...parameters) {
        Objects.requireNonNull(message);
        final String formattedMessage;
        if ((parameters != null) && (parameters.length > 0)) {
            formattedMessage = MessageFormatter.arrayFormat(message, parameters).getMessage();
        } else {
            formattedMessage = message;
        }
        final int listenersCount = taskListeners.size();
        if (listenersCount == 0) {
            LOGGER.info("Message received for task [{}], but no listeners are registered. Message is [{}].", name, formattedMessage);
            return;
        }
        LOGGER.info("Task [{}] sending event for message [{}] to [{}] listeners ...", name, formattedMessage, listenersCount);
        sendTaskMessageEvent(message);
    }

    protected void sendTaskMessageEvent(final String message) {
        notifyTaskListeners(listener -> {
            try {
                listener.handleTaskMessage(message);
            } catch (final RuntimeException error) {
                sendError(TaskErrorType.LISTENER_NOTIFICATION_FAILED, error, name);
            }
        });
    }

    protected void sendCompletionStatus(final TaskEndStatus executionStatus) {
        Objects.requireNonNull(executionStatus);
        final int listenersCount = completionListeners.size();
        if (listenersCount == 0) {
            return;
        }
        LOGGER.info("Task [{}] sending completion event with status [{}] to [{}] listeners ...", name, executionStatus, listenersCount);
        sendTaskCompletionEvent(executionStatus);
    }

    protected void sendTaskCompletionEvent(final TaskEndStatus executionStatus) {
        notifyTaskCompletionListeners(listener -> listener.handleTaskFinished(executionStatus));
    }

    protected void sendError(final TaskErrorType errorType, final Exception error, final Object...parameters) {
        Objects.requireNonNull(errorType);
        Objects.requireNonNull(error);
        final int listenersCount = taskListeners.size();
        if (listenersCount == 0) {
            LOGGER.error("An error occured during the [{}] status of the action [{}], but no listeners are registered to receive the error.", status, name, error);
            return;
        }
        LOGGER.error("An error occured during the [{}] status of the action [{}] ...", status, name, error);
        if (status == TaskStatus.STARTED) {
            executionFailed = true;
        }
        final TaskException exception = new TaskException(errorType, error, parameters);
        sendTaskException(exception);
    }

    protected void sendTaskException(final TaskException exception) {
        notifyTaskListeners(listener -> listener.handleTaskError(exception));
    }

    @Override
    public void perform() {
        LOGGER.info("Starting action [{}] ...", name);
        executionFailed = false;
        stopAsked = false;
        setStatus(TaskStatus.STARTING);
        try {
            if (! beforeAction()) {
                abortTask();
                return;
            }
        } catch (final RuntimeException error) {
            sendError(TaskErrorType.TASK_INITIALIZATION_FAILED, error);
            return;
        }
        if (isStopAsked()) {
            abortTask();
        } else {
            startTask();
        }
    }

    protected void abortTask() {
        LOGGER.info("Aborting task [{}] ...", name);
        sendStatusChange(getStatus(), TaskStatus.STOPPED);
        sendCompletionStatus(TaskEndStatus.ABORTED);
    }

    protected void startTask() {
        if (isStopAsked()) {
            abortTask();
            return;
        }
        try {
            LOGGER.info("Performing action [{}] ...", name);
            setStatus(TaskStatus.STARTED);
            performAction();
            LOGGER.info("Action [{}] performed.", name);
        } catch (final RuntimeException error) {
            executionFailed = true;
            sendError(TaskErrorType.TASK_EXECUTION_FAILED, error, name);
        }
        setStatus(TaskStatus.STOPPING);
        try {
            afterAction();
        } catch (final RuntimeException error) {
            sendError(TaskErrorType.TASK_CLEANING_FAILED, error, name);
        }
        setStatus(TaskStatus.STOPPED);
        sendCompletionStatus(executionFailed ? TaskEndStatus.EXECUTION_FAILED : TaskEndStatus.EXECUTION_SUCCESS);
    }

    /**
     * Invoked just before the action is performed to check if it can actually be executed.
     *
     * @return true if the action execution should continue
     */
    protected boolean beforeAction() {
        return true;
    }

    protected abstract void performAction();

    protected void afterAction() {}

    public boolean isExecutionFailed() {
        return executionFailed;
    }

    public void notifyTaskListeners(final Consumer<TaskListener> callback) {
        for (final TaskListener listener : taskListeners) {
            callback.accept(listener);
        }
    }

    public void notifyTaskCompletionListeners(final Consumer<TaskCompletionListener> callback) {
        for (final TaskCompletionListener listener : completionListeners) {
            callback.accept(listener);
        }
    }

    @Override
    public void addTaskListener(final TaskListener listener) {
        Objects.requireNonNull(listener);
        taskListeners.add(listener);
    }

    @Override
    public void removeTaskListener(final TaskListener listener) {
        Objects.requireNonNull(listener);
        if (! taskListeners.remove(listener)) {
            throw new IllegalArgumentException("listener has not been added to this task, it can't be removed");
        }
    }

    @Override
    public void addTaskCompletionListener(final TaskCompletionListener listener) {
        Objects.requireNonNull(listener);
        completionListeners.add(listener);
    }

    @Override
    public void removeTaskCompletionListener(final TaskCompletionListener listener) {
        Objects.requireNonNull(listener);
        if (! completionListeners.remove(listener)) {
            throw new IllegalArgumentException("listener has not been added to this task, it can't be removed");
        }
    }

    public static SynchronousTask create(final String name, final Runnable action) {
        return new SynchronousTask(name) {
            @Override
            protected void performAction() {
                action.run();
            }
        };
    }
}