<!--
  - MIT License
  -
  - Copyright © 2020-2023 dev-toolbox.org
  -
  - Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
  - (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
  - distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
  - following conditions:
  -
  - The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
  -
  - THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  - MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
  - CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
  - OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  -->

dev-toolbox-util-task
=====================

Task sytem with listeners, status change, messages. Both synchronous and asynchronous implementations are available.

history
-------
- v0.10.0 2023/02/12 : added task listener implementation forwarding to slf4j and sendError method for generic exceptions
- v0.9.1  2023/02/05 : upgraded util-exception to v0.9.1
- v0.9.0  2023/01/29 : java 17
- v0.8.3  2020/12/05 : upgraded util-exception to v8.0.0
- v0.8.2  2020/04/29 : changed TaskException constructor with varargs
- v0.8.1  2020/04/28 : cast getIdentifier to TaskErrorType in TaskException
- v0.8.0  2020/04/21 : java 14
- previous versions : history lost :)