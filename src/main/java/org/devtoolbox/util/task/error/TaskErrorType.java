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
package org.devtoolbox.util.task.error;

import org.devtoolbox.util.exception.ErrorIdentifier;


/**
 * @author Arnaud Lecollaire
 */
public enum TaskErrorType implements ErrorIdentifier {
	TASK_INITIALIZATION_FAILED("Task [{}] : initialization failed"),
	LISTENER_NOTIFICATION_FAILED("An error occured while sending a notification to a listener for task [{}]."),
	TASK_EXECUTION_FAILED("Task [{}] : execution failed"),
	TASK_CLEANING_FAILED("Task [{}] : cleaning failed"),
	ERROR_IN_TIMEOUT_MONITOR("Task [{}] : error while trying to monitor task for timeout");

	private final String defaultMessage;

	private TaskErrorType(final String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getDefaultMessage() {
		return defaultMessage;
	}
}