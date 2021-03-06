/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.security.interfaces;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

@TestTargetClass(RSAPublicKey.class)
public class RSAPublicKeyTest extends TestCase {

    /**
     * @tests java.security.interfaces.RSAPublicKey
     * #getPublicExponent()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getPublicExponent",
        args = {}
    )
    public void test_getPublicExponent() throws Exception {
        KeyFactory gen = KeyFactory.getInstance("RSA");
        final BigInteger n = BigInteger.valueOf(3233);
        final BigInteger e = BigInteger.valueOf(17);
        RSAPublicKey key = (RSAPublicKey) gen.generatePublic(new RSAPublicKeySpec(
                n, e));
        assertEquals("invalid public exponent", e, key.getPublicExponent());
    }
}
