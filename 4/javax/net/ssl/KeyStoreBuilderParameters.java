/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package javax.net.ssl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.KeyStore;

/**
 * The parameters for {@code KeyManager}s. The parameters are a list of
 * {@code KeyStore.Builder}s.
 * 
 * @since Android 1.0
 */
public class KeyStoreBuilderParameters implements ManagerFactoryParameters {

    private List ksbuilders;

    /**
     * Creates a new {@code KeyStoreBuilderParameters} with the specified key
     * store builder.
     * 
     * @param builder
     *            the key store builder.
     * @since Android 1.0
     */
    public KeyStoreBuilderParameters(KeyStore.Builder builder) {
        ksbuilders = new ArrayList();
        if (builder != null) {
            ksbuilders.add(builder);
        }
    }

    /**
     * Creates a new {@code KeyStoreBuilderParameters} with the specified list
     * of {@code KeyStore.Builder}s.
     * 
     * @param parameters
     *            the list of key store builders
     * @throws IllegalArgumentException
     *             if the specified list is empty.
     * @since Android 1.0
     */
    public KeyStoreBuilderParameters(List parameters) {
        if (parameters == null) {
            throw new NullPointerException("Builders list is null");
        }
        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("Builders list is empty");
        }
        ksbuilders = new ArrayList(parameters);
    }

    /**
     * Returns the unmodifiable list of {@code KeyStore.Builder}s associated
     * with this parameters instance.
     * 
     * @return the unmodifiable list of {@code KeyStore.Builder}s.
     * @since Android 1.0
     */
    public List getParameters() {
        return Collections.unmodifiableList(ksbuilders);
    }
}
