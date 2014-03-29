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

/**
* @author Vera Y. Petrashkova
* @version $Revision$
*/

package java.security;

import java.security.spec.AlgorithmParameterSpec;

import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.internal.nls.Messages;

/**
 * {@code AlgorithmParameterGenerator} is an engine class which is capable of
 * generating parameters for the algorithm it was initialized with.
 * 
 * @since Android 1.0
 */
public class AlgorithmParameterGenerator {

    // Store spi service name
    private static final String SERVICE = "AlgorithmParameterGenerator"; //$NON-NLS-1$

    // Used to access common engine functionality
    private static Engine engine = new Engine(SERVICE);

    // Store SecureRandom
    private static SecureRandom randm = new SecureRandom();

    // Store used provider
    private final Provider provider;

    // Store used AlgorithmParameterGeneratorSpi implementation
    private final AlgorithmParameterGeneratorSpi spiImpl;

    //Store used algorithm
    private final String algorithm;

    /**
     * Constructs a new instance of {@code AlgorithmParameterGenerator} with the
     * given arguments.
     * 
     * @param paramGenSpi
     *            a concrete implementation, this engine instance delegates to.
     * @param provider
     *            the provider.
     * @param algorithm
     *            the name of the algorithm.
     * @since Android 1.0
     */
    protected AlgorithmParameterGenerator(
            AlgorithmParameterGeneratorSpi paramGenSpi, Provider provider,
            String algorithm) {
        this.provider = provider;
        this.algorithm = algorithm;
        this.spiImpl = paramGenSpi;
    }

    /**
     * Returns the name of the algorithm.
     * 
     * @return the name of the algorithm.
     * @since Android 1.0
     */
    public final String getAlgorithm() {
        return algorithm;
    }

    /**
     * Returns a new instance of {@code AlgorithmParameterGenerator} for the
     * specified algorithm.
     * 
     * @param algorithm
     *            the name of the algorithm to use.
     * @return a new instance of {@code AlgorithmParameterGenerator} for the
     *         specified algorithm.
     * @throws NoSuchAlgorithmException
     *             if the specified algorithm is not available.
     * @throws NullPointerException
     *             if {@code algorithm} is {@code null}.
     * @since Android 1.0
     */
    public static AlgorithmParameterGenerator getInstance(String algorithm)
            throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException(Messages.getString("security.01")); //$NON-NLS-1$
        }
        synchronized (engine) {
            engine.getInstance(algorithm, null);
            return new AlgorithmParameterGenerator(
                    (AlgorithmParameterGeneratorSpi) engine.spi, engine.provider,
                    algorithm);
        }
    }

    /**
     * Returns a new instance of {@code AlgorithmParameterGenerator} from the
     * specified provider for the specified algorithm.
     * 
     * @param algorithm
     *            the name of the algorithm to use.
     * @param provider
     *            name of the provider of the {@code
     *            AlgorithmParameterGenerator}.
     * @return a new instance of {@code AlgorithmParameterGenerator} for the
     *         specified algorithm.
     * @throws NoSuchAlgorithmException
     *             if the specified algorithm is not available.
     * @throws NoSuchProviderException
     *             if the specified provider is not available.
     * @throws NullPointerException
     *             if {@code algorithm} is {@code null}.
     * @since Android 1.0
     */
    public static AlgorithmParameterGenerator getInstance(String algorithm,
            String provider) throws NoSuchAlgorithmException,
            NoSuchProviderException {
        if ((provider == null) || (provider.length() == 0)) {
            throw new IllegalArgumentException(
                    Messages.getString("security.02")); //$NON-NLS-1$
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider == null) {
            throw new NoSuchProviderException(provider);
        }
        return getInstance(algorithm, impProvider);
    }

    /**
     * Returns a new instance of {@code AlgorithmParameterGenerator} from the
     * specified provider for the specified algorithm.
     * 
     * @param algorithm
     *            the name of the algorithm to use.
     * @param provider
     *            the provider of the {@code AlgorithmParameterGenerator}.
     * @return a new instance of {@code AlgorithmParameterGenerator} for the
     *         specified algorithm.
     * @throws NoSuchAlgorithmException
     *             if the specified algorithm is not available.
     * @throws NullPointerException
     *             if {@code algorithm} is {@code null}.
     * @since Android 1.0
     */
    public static AlgorithmParameterGenerator getInstance(String algorithm,
            Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException(Messages.getString("security.04")); //$NON-NLS-1$
        }
        if (algorithm == null) {
            throw new NullPointerException(Messages.getString("security.01")); //$NON-NLS-1$
        }
        synchronized (engine) {
            engine.getInstance(algorithm, provider, null);
            return new AlgorithmParameterGenerator(
                    (AlgorithmParameterGeneratorSpi) engine.spi, provider,
                    algorithm);
        }
    }

    /**
     * Returns the provider associated with this {@code
     * AlgorithmParameterGenerator}.
     * 
     * @return the provider associated with this {@code
     *         AlgorithmParameterGenerator}.
     * @since Android 1.0
     */
    public final Provider getProvider() {
        return provider;
    }

    /**
     * Initializes this {@code AlgorithmParameterGenerator} with the given size.
     * The default parameter set and a default {@code SecureRandom} instance
     * will be used.
     * 
     * @param size
     *            the size (in number of bits).
     * @since Android 1.0
     */
    public final void init(int size) {
        spiImpl.engineInit(size, randm);
    }

    /**
     * Initializes this {@code AlgorithmParameterGenerator} with the given size
     * and the given {@code SecureRandom}. The default parameter set will be
     * used.
     * 
     * @param size
     *            the size (in number of bits).
     * @param random
     *            the source of randomness.
     * @since Android 1.0
     */
    public final void init(int size, SecureRandom random) {
        spiImpl.engineInit(size, random);
    }

    /**
     * Initializes this {@code AlgorithmParameterGenerator} with the given {@code
     * AlgorithmParameterSpec}. A default {@code SecureRandom} instance will be
     * used.
     * 
     * @param genParamSpec
     *            the parameters to use.
     * @throws InvalidAlgorithmParameterException
     *             if the specified parameters are not supported.
     * @since Android 1.0
     */
    public final void init(AlgorithmParameterSpec genParamSpec)
            throws InvalidAlgorithmParameterException {
        spiImpl.engineInit(genParamSpec, randm);
    }

    /**
     * Initializes this {@code AlgorithmParameterGenerator} with the given
     * {@code AlgorithmParameterSpec} and the given {@code SecureRandom}.
     * 
     * @param genParamSpec
     *            the parameters to use.
     * @param random
     *            the source of randomness.
     * @throws InvalidAlgorithmParameterException
     *             if the specified parameters are not supported.
     * @since Android 1.0
     */
    public final void init(AlgorithmParameterSpec genParamSpec,
            SecureRandom random) throws InvalidAlgorithmParameterException {
        spiImpl.engineInit(genParamSpec, random);
    }

    /**
     * Computes and returns {@code AlgorithmParameters} for this generator's
     * algorithm.
     * 
     * @return {@code AlgorithmParameters} for this generator's algorithm.
     * @since Android 1.0
     */
    public final AlgorithmParameters generateParameters() {
        return spiImpl.engineGenerateParameters();
    }
}