/*
 * Copyright (C) 2009 Marc Blank
 * Licensed to The Android Open Source Project.
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

package com.android.exchange;

import android.content.Context;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.IOException;

public class EasSyncServiceTests extends AndroidTestCase {
    Context mMockContext;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mMockContext = getContext();
    }

   /**
     * Test that our unique file name algorithm works as expected.
     * @throws IOException
     */
    public void testCreateUniqueFile() throws IOException {
        // Delete existing files, if they exist
        EasSyncService svc = new EasSyncService();
        svc.mContext = mMockContext;
        try {
            String fileName = "A11achm3n1.doc";
            File uniqueFile = svc.createUniqueFileInternal(null, fileName);
            assertEquals(fileName, uniqueFile.getName());
            if (uniqueFile.createNewFile()) {
                uniqueFile = svc.createUniqueFileInternal(null, fileName);
                assertEquals("A11achm3n1-2.doc", uniqueFile.getName());
                if (uniqueFile.createNewFile()) {
                    uniqueFile = svc.createUniqueFileInternal(null, fileName);
                    assertEquals("A11achm3n1-3.doc", uniqueFile.getName());
                }
           }
            fileName = "A11achm3n1";
            uniqueFile = svc.createUniqueFileInternal(null, fileName);
            assertEquals(fileName, uniqueFile.getName());
            if (uniqueFile.createNewFile()) {
                uniqueFile = svc.createUniqueFileInternal(null, fileName);
                assertEquals("A11achm3n1-2", uniqueFile.getName());
            }
        } finally {
            // These are the files that should be created earlier in the test.  Make sure
            // they are deleted for the next go-around
            File directory = getContext().getFilesDir();
            String[] fileNames = new String[] {"A11achm3n1.doc", "A11achm3n1-2.doc", "A11achm3n1"};
            int length = fileNames.length;
            for (int i = 0; i < length; i++) {
                File file = new File(directory, fileNames[i]);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }


}
