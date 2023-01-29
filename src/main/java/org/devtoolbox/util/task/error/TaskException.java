/*
 * MIT License
 *
 * Copyright © 2020-2023 dev-toolbox.org
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

import org.devtoolbox.util.exception.ParametricCheckedException;


/**
 * @author Arnaud Lecollaire
 */
public class TaskException extends ParametricCheckedException {

	private static final long serialVersionUID = 900828329673700007L;


    public TaskException(final TaskErrorType identifier, final Exception error, final Object...parameters) {
        super(identifier, error, parameters);
    }

    @Override
    public TaskErrorType getIdentifier() {
        return (TaskErrorType) super.getIdentifier();
    }
}