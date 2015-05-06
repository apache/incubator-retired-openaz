/**
 * Copyright 2009-2011 Oracle, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 *        1.1 (2011): Rich Levinson, Prateek Mishra (Oracle)
 *        1.0 (2009): Josh Bregman, Rich Levinson, Prateek Mishra (Oracle)
 * Contributor:
 *        Rich Levinson (Oracle)
 */
package org.openliberty.openaz.pepapi;

import java.util.List;

/**
 *
 *
 */
public interface PepRequestFactory {

    /**
     * @return
     * @throws org.openliberty.openaz.pepapi.PepException, if no ObjectMappers found.
     * @throws IllegalArgumentException,, if any argument is null.
     */
    PepRequest newPepRequest(Object[] objects);

    /**
     * @param associations
     * @param objects
     * @return
     * @throws org.openliberty.openaz.pepapi.PepException, if ObjectMappers are not found.
     * @throws IllegalArgumentException,, if the arguments are null.
     */
    PepRequest newBulkPepRequest(List<?> associations, Object[] objects);

}
