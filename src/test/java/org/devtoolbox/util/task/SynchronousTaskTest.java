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

/**
 * @author Arnaud Lecollaire
 */
package org.devtoolbox.util.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.devtoolbox.util.task.error.TaskException;
import org.devtoolbox.util.task.implementation.SynchronousTask;
import org.devtoolbox.util.task.listener.TaskCompletionListener;
import org.devtoolbox.util.task.listener.TaskListener;
import org.devtoolbox.util.task.status.TaskEndStatus;
import org.devtoolbox.util.task.status.TaskStatus;
import org.junit.jupiter.api.Test;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;


public class SynchronousTaskTest {

    private static final TaskStatus[] LIFECYCLE_STATUS = new TaskStatus[] {
        TaskStatus.CREATED, TaskStatus.INITIALIZED, TaskStatus.STARTING, TaskStatus.STARTED, TaskStatus.STOPPING, TaskStatus.STOPPED };

    private static final TaskStatus[] REMOVE_LISTENERS_LIFECYCLE_STATUS = new TaskStatus[] {
        TaskStatus.CREATED, TaskStatus.INITIALIZED, TaskStatus.STARTING, TaskStatus.STARTED };

    private static final TaskStatus[] ABORT_LIFECYCLE_STATUS = new TaskStatus[] {
        TaskStatus.CREATED, TaskStatus.INITIALIZED, TaskStatus.STARTING, TaskStatus.STOPPED };


    @Test
    public void statusTest() {
        try {
            final Collection<TaskStatus> status = new ArrayList<>();
            final SynchronousTask testTask = new TestTask() {
                {
                    status.add(getStatus());
                }
                @Override
                protected void initializeTask() {
                    status.add(getStatus());
                }
            };
            final BooleanProperty errorReceivedProperty = new SimpleBooleanProperty();
            final ObjectProperty<TaskEndStatus> taskEndStatusProperty = new SimpleObjectProperty<>();
            testTask.addTaskListener(new TaskListener() {
                @Override
                public void handleTaskStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
                    status.add(newStatus);
                }
                @Override
                public void handleTaskError(final TaskException error) {
                    errorReceivedProperty.set(true);
                }
            });
            testTask.addTaskCompletionListener(endStatus -> taskEndStatusProperty.set(endStatus));
            testTask.perform();
            assertArrayEquals(LIFECYCLE_STATUS, status.toArray());
            assertFalse(errorReceivedProperty.get());
            assertEquals(TaskEndStatus.EXECUTION_SUCCESS, taskEndStatusProperty.get());
        } catch (final RuntimeException error) {
            error.printStackTrace();
            fail("An error should not have been thrown.");
        }
    }

    @Test
    public void removeListenerTest() {
        try {
            final Collection<TaskStatus> status = new ArrayList<>();
            final BooleanProperty errorReceivedProperty = new SimpleBooleanProperty();
            final ObjectProperty<TaskEndStatus> taskEndStatusProperty = new SimpleObjectProperty<>();
            final TaskListener taskListener = new TaskListener() {
                @Override
                public void handleTaskStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
                    status.add(newStatus);
                }
                @Override
                public void handleTaskError(final TaskException error) {
                    errorReceivedProperty.set(true);
                }
            };
            final TaskCompletionListener taskCompletionListener = endStatus -> taskEndStatusProperty.set(endStatus);
            final SynchronousTask testTask = new TestTask() {
                {
                    status.add(getStatus());
                }
                @Override
                protected void initializeTask() {
                    status.add(getStatus());
                }
                @Override
                protected void performAction() {
                    removeTaskListener(taskListener);
                    removeTaskCompletionListener(taskCompletionListener);
                };
            };
            testTask.addTaskListener(taskListener);
            testTask.addTaskCompletionListener(taskCompletionListener);
            testTask.perform();
            assertArrayEquals(REMOVE_LISTENERS_LIFECYCLE_STATUS, status.toArray());
            assertFalse(errorReceivedProperty.get());
            assertEquals(null, taskEndStatusProperty.get());
        } catch (final RuntimeException error) {
            error.printStackTrace();
            fail("An error should not have been thrown.");
        }
    }

    @Test
    public void errorTest() {
        try {
            final Collection<TaskStatus> status = new ArrayList<>();
            final SynchronousTask testTask = new TestTask() {
                {
                    status.add(getStatus());
                }
                @Override
                protected void initializeTask() {
                    status.add(getStatus());
                }
                @Override
                protected void performAction() {
                    throw new NullPointerException();
                }
            };
            final BooleanProperty errorReceivedProperty = new SimpleBooleanProperty();
            final ObjectProperty<TaskEndStatus> taskEndStatusProperty = new SimpleObjectProperty<>();
            testTask.addTaskListener(new TaskListener() {
                @Override
                public void handleTaskStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
                    status.add(newStatus);
                }
                @Override
                public void handleTaskError(final TaskException error) {
                    errorReceivedProperty.set(true);
                }
            });
            testTask.addTaskCompletionListener(endStatus -> taskEndStatusProperty.set(endStatus));
            testTask.perform();
            assertArrayEquals(LIFECYCLE_STATUS, status.toArray());
            assertTrue(errorReceivedProperty.get());
            assertEquals(TaskEndStatus.EXECUTION_FAILED, taskEndStatusProperty.get());
        } catch (final RuntimeException error) {
            error.printStackTrace();
            fail("An error should not have been thrown.");
        }
    }

    @Test
    public void abortTest() {
        try {
            final Collection<TaskStatus> status = new ArrayList<>();
            final SynchronousTask testTask = new TestTask() {
                {
                    status.add(getStatus());
                }
                @Override
                protected void initializeTask() {
                    status.add(getStatus());
                }
                @Override
                protected boolean beforeAction() {
                    return false;
                }
            };
            final BooleanProperty errorReceivedProperty = new SimpleBooleanProperty();
            final ObjectProperty<TaskEndStatus> taskEndStatusProperty = new SimpleObjectProperty<>();
            testTask.addTaskListener(new TaskListener() {
                @Override
                public void handleTaskStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
                    status.add(newStatus);
                }
                @Override
                public void handleTaskError(final TaskException error) {
                    errorReceivedProperty.set(true);
                }
            });
            testTask.addTaskCompletionListener(endStatus -> taskEndStatusProperty.set(endStatus));
            testTask.perform();
            assertArrayEquals(ABORT_LIFECYCLE_STATUS, status.toArray());
            assertFalse(errorReceivedProperty.get());
            assertEquals(TaskEndStatus.ABORTED, taskEndStatusProperty.get());
        } catch (final RuntimeException error) {
            error.printStackTrace();
            fail("An error should not have been thrown.");
        }
    }

    @Test
    public void messageTest() {
        try {
            final Collection<TaskStatus> status = new ArrayList<>();
            final String messageInitializing = "message initializing";
            final String messageInitialized = "message initialized";
            final String messageStarting = "message starting";
            final String messageStarted = "message started";
            final String messageStopping = "message stopping";
            final BooleanProperty errorReceivedProperty = new SimpleBooleanProperty();
            final ObjectProperty<TaskEndStatus> taskEndStatusProperty = new SimpleObjectProperty<>();
            final ObjectProperty<Task> taskProperty = new SimpleObjectProperty<>();

            final TaskListener taskListener = new TaskListener() {
                @Override
                public void handleTaskStatusChange(final TaskStatus oldStatus, final TaskStatus newStatus) {
                    status.add(newStatus);
                }
                @Override
                public void handleTaskError(final TaskException error) {
                    errorReceivedProperty.set(true);
                }
                @Override
                public void handleTaskMessage(final String message) {
                    switch (message) {
                    case messageInitializing:
                        assertEquals(TaskStatus.CREATED, taskProperty.get().getStatus());
                        break;
                    case messageInitialized:
                        assertEquals(TaskStatus.INITIALIZED, taskProperty.get().getStatus());
                        break;
                    case messageStarting:
                        assertEquals(TaskStatus.STARTING, taskProperty.get().getStatus());
                        break;
                    case messageStarted:
                        assertEquals(TaskStatus.STARTED, taskProperty.get().getStatus());
                        break;
                    case messageStopping:
                        assertEquals(TaskStatus.STOPPING, taskProperty.get().getStatus());
                        break;
                    default:
                        fail("Unexpected message received : [" + message + "].");
                    }
                }
            };

            final SynchronousTask testTask = new TestTask() {
                {
                    sendTaskMessage(messageInitialized);
                }
                @Override
                protected void initializeTask() {
                    taskProperty.set(this);
                    addTaskListener(taskListener);
                    status.add(getStatus());
                    sendTaskMessage(messageInitializing);
                }
                @Override
                protected boolean beforeAction() {
                    sendTaskMessage(messageStarting);
                    return true;
                }
                @Override
                protected void performAction() {
                    sendTaskMessage(messageStarted);
                }
                @Override
                protected void afterAction() {
                    sendTaskMessage(messageStopping);
                }
            };

            testTask.addTaskCompletionListener(endStatus -> taskEndStatusProperty.set(endStatus));
            testTask.perform();
            assertArrayEquals(LIFECYCLE_STATUS, status.toArray());
            assertFalse(errorReceivedProperty.get());
            assertEquals(TaskEndStatus.EXECUTION_SUCCESS, taskEndStatusProperty.get());
        } catch (final RuntimeException error) {
            error.printStackTrace();
            fail("An error should not have been thrown.");
        }
    }


    protected static class TestTask extends SynchronousTask {

        protected TestTask() {
            super("test task");
            assertEquals(TaskStatus.INITIALIZED, getStatus());
        }

        @Override
        protected void initializeTask() {
            super.initializeTask();
            assertEquals(TaskStatus.CREATED, getStatus());
        }

        @Override
        protected void performAction() {
        }
    }
}