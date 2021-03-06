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

package android.view.cts;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.test.AndroidTestCase;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.android.cts.stub.R;
import com.android.internal.util.XmlUtils;

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

@TestTargetClass(ViewGroup.LayoutParams.class)
public class ViewGroup_LayoutParamsTest extends AndroidTestCase {
    private ViewGroup.LayoutParams mLayoutParams;

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "ViewGroup.LayoutParams",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "ViewGroup.LayoutParams",
            args = {android.content.Context.class, android.util.AttributeSet.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "ViewGroup.LayoutParams",
            args = {android.view.ViewGroup.LayoutParams.class}
        )
    })
    public void testConstructor() throws XmlPullParserException, IOException {
        // new the MarginLayoutParams instance
        XmlResourceParser parser = mContext.getResources().getLayout(
                R.layout.viewgroup_margin_layout);

        XmlUtils.beginDocument(parser, "LinearLayout");
        new ViewGroup.LayoutParams(mContext, parser);

        LayoutParams temp = new ViewGroup.LayoutParams(320, 480);

        new ViewGroup.LayoutParams(temp);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test setBaseAttributes(TypedArray, int, int)",
        method = "setBaseAttributes",
        args = {android.content.res.TypedArray.class, int.class, int.class}
    )
    public void testSetBaseAttributes() throws XmlPullParserException, IOException {
        MockLayoutParams mockLayoutParams = new MockLayoutParams(240, 320);

        int[] attrs = R.styleable.style1;
        TypedArray array = mContext.getTheme().obtainStyledAttributes(R.style.Whatever, attrs);
        mockLayoutParams.setBaseAttributes(array, R.styleable.style1_type6,
                R.styleable.style1_type7);
        int defValue = -1;
        assertEquals(array.getDimensionPixelSize(R.styleable.style1_type6, defValue),
                mockLayoutParams.width);
        assertEquals(array.getDimensionPixelSize(R.styleable.style1_type7, defValue),
                mockLayoutParams.height);
        array.recycle();
    }

    private class MockLayoutParams extends LayoutParams {
        public MockLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MockLayoutParams(int width, int height) {
            super(width, height);
        }

        public MockLayoutParams(LayoutParams source) {
            super(source);
        }

        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            super.setBaseAttributes(a, widthAttr, heightAttr);
        }
    }
}
