/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package signature.comparator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import signature.converter.dex.DexTestConverter;
import signature.converter.util.ITestSourceConverter;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AllDexTests.DexPackageCompareTest.class,
    AllDexTests.DexClassCompareTest.class,
    AllDexTests.DexMethodCompareTests.class,
    AllDexTests.DexAnnotationCompareTest.class
})
public class AllDexTests {
    private static ITestSourceConverter newConverter(){
        return new DexTestConverter();
    }
    
    public static class DexPackageCompareTest extends PackageCompareTest {
        @Override public ITestSourceConverter createConverter() {
            return newConverter();
        }
    }
    
    public static class DexClassCompareTest extends ClassCompareTest {
        @Override public ITestSourceConverter createConverter() {
            return newConverter();
        }
    }
    
    public static class DexMethodCompareTests extends MethodCompareTests {
        @Override public ITestSourceConverter createConverter() {
            return newConverter();
        }
    }
    
    public static class DexAnnotationCompareTest extends AnnotationCompareTest{
        @Override public ITestSourceConverter createConverter() {
            return newConverter();
        }
    }
    
    
}