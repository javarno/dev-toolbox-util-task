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
package org.devtoolbox.util.task.status;


/**
 * @author Arnaud Lecollaire
 */
public enum TaskStatus {
	/** task has been created, but no parameters have been defined yet */
	CREATED,
	/** all required parameters have been defined for this task (they can still change before it's started, but in its current state, the task can start) */
	INITIALIZED,
	/** task is starting, it may perform some initialization () before actually starting to work */
	STARTING,
	/** task is currently working */
	STARTED,
	/** task is stopping, it will clear all used resources before reaching the STOPPED status */
	STOPPING,
	/** task is stopped */
	STOPPED,
	/** task has been running for longer than expected (but it is not stopped, and could finish normally) */
	TIMEOUT
}