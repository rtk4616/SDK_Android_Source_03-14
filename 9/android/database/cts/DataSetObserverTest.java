/*
 * Copyright (C) 2008 The Android Open Source Project
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

package android.database.cts;

import android.database.DataSetObserver;
import android.test.AndroidTestCase;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

@TestTargetClass(android.database.DataSetObserver.class)
public class DataSetObserverTest extends AndroidTestCase {
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test onChanged, and this is an empty method.",
        method = "onChanged",
        args = {}
    )
    public void testOnChanged() {
        MockDataSetObserver dataSetObserver = new MockDataSetObserver();
        dataSetObserver.onChanged();
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test OnInvalidated, and this is an empty method.",
        method = "onInvalidated",
        args = {}
    )
    public void testOnInvalidated() {
        MockDataSetObserver dataSetObserver = new MockDataSetObserver();
        dataSetObserver.onInvalidated();
    }

    private class MockDataSetObserver extends DataSetObserver {
    }
}
