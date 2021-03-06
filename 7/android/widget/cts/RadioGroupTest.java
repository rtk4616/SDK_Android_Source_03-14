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

package android.widget.cts;

import com.android.cts.stub.R;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.ToBeFixed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;

import java.io.IOException;
import java.util.Vector;

/**
 * Test {@link RadioGroup}.
 */
@TestTargetClass(RadioGroup.class)
public class RadioGroupTest extends InstrumentationTestCase {
    private static final int BUTTON_ID_0 = 0;

    private static final int BUTTON_ID_1 = 1;

    private static final int BUTTON_ID_2 = 2;

    private static final int BUTTON_ID_3 = 3;

    /** the IDs of the buttons inside the group are 0, 1, 2, 3. */
    private RadioGroup mDefaultRadioGroup;

    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getTargetContext();
        // the IDs of the buttons inside the group are 0, 1, 2, 3
        mDefaultRadioGroup = createDefaultRadioGroup();
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test constructors.",
            method = "RadioGroup",
            args = {android.content.Context.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test constructors.",
            method = "RadioGroup",
            args = {android.content.Context.class, android.util.AttributeSet.class}
        )
    })
    @ToBeFixed(bug = "1417734", explanation = "should add @throws clause into javadoc of "
            + "RadioGroup#RadioGroup(Context, AttributeSet) when param Context is null")
    public void testConstructors() {
        new RadioGroup(mContext);
        new RadioGroup(null);

        AttributeSet attrs = getAttributeSet(R.layout.radiogroup_1);
        new RadioGroup(mContext, attrs);
        try {
            new RadioGroup(null, attrs);
            fail("The constructor should throw NullPointerException when param Context is null.");
        } catch (NullPointerException e) {
        }
        new RadioGroup(mContext, null);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#setOnHierarchyChangeListener(OnHierarchyChangeListener)",
        method = "setOnHierarchyChangeListener",
        args = {android.view.ViewGroup.OnHierarchyChangeListener.class}
    )
    public void testSetOnHierarchyChangeListener() {
        MockOnHierarchyChangeListener listener = new MockOnHierarchyChangeListener();
        mDefaultRadioGroup.setOnHierarchyChangeListener(listener);

        View button3 = mDefaultRadioGroup.findViewById(BUTTON_ID_3);
        listener.reset();
        mDefaultRadioGroup.removeView(button3);
        assertSame(mDefaultRadioGroup, listener.getOnChildViewRemovedParentParam());
        assertSame(button3, listener.getOnChildViewRemovedChildParam());

        listener.reset();
        mDefaultRadioGroup.addView(button3);
        assertSame(mDefaultRadioGroup, listener.getOnChildViewAddedParentParam());
        assertSame(button3, listener.getOnChildViewAddedChildParam());

        // Set listener to null
        mDefaultRadioGroup.setOnHierarchyChangeListener(null);
        // and no exceptions thrown in the following method calls
        mDefaultRadioGroup.removeView(button3);
        mDefaultRadioGroup.addView(button3);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "test PassThroughHierarchyChangeListener which is initialized in constructor",
        method = "RadioGroup",
        args = {android.content.Context.class}
    )
    public void testInternalPassThroughHierarchyChangeListener() {
        mDefaultRadioGroup = new RadioGroup(mContext);
        RadioButton newButton = new RadioButton(mContext);

        assertEquals(View.NO_ID, newButton.getId());
        mDefaultRadioGroup.addView(newButton, new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        // set the id with hashCode
        // (PassThroughHierarchyChangeListener's behaviour when button is added)
        assertEquals(newButton.hashCode(), newButton.getId());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test call back of OnCheckedChangeListener",
        method = "setOnCheckedChangeListener",
        args = {android.widget.RadioGroup.OnCheckedChangeListener.class}
    )
    public void testInternalCheckedStateTracker() {
        mDefaultRadioGroup = new RadioGroup(mContext);
        RadioButton newButton = new RadioButton(mContext);
        // inject the tracker to the button when the button is added by
        // CompoundButton#setOnCheckedChangeWidgetListener(OnCheckedChangeListener)
        mDefaultRadioGroup.addView(newButton, new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        MockOnCheckedChangeListener listener = new MockOnCheckedChangeListener();
        mDefaultRadioGroup.setOnCheckedChangeListener(listener);

        listener.reset();
        newButton.setChecked(true);
        // the tracker informs the checked state change of the button to the group
        assertHasCalledOnCheckedChanged(listener);

        listener.reset();
        // the tracker informs the checked state change of the button to the group
        newButton.setChecked(false);
        assertHasCalledOnCheckedChanged(listener);

        // remove the tracker from the button when the button is removed
        mDefaultRadioGroup.removeView(newButton);
        listener.reset();
        newButton.setChecked(true);
        assertHaveNotCalledOnCheckedChanged(listener);

        listener.reset();
        newButton.setChecked(false);
        assertHaveNotCalledOnCheckedChanged(listener);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#getCheckedRadioButtonId()}",
        method = "getCheckedRadioButtonId",
        args = {}
    )
    public void testGetCheckedRadioButtonId() {
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());

        mDefaultRadioGroup.check(BUTTON_ID_0);
        assertEquals(BUTTON_ID_0, mDefaultRadioGroup.getCheckedRadioButtonId());

        mDefaultRadioGroup.check(BUTTON_ID_3);
        assertEquals(BUTTON_ID_3, mDefaultRadioGroup.getCheckedRadioButtonId());

        // None of the buttons inside the group has of of the following IDs
        mDefaultRadioGroup.check(4);
        assertEquals(4, mDefaultRadioGroup.getCheckedRadioButtonId());

        mDefaultRadioGroup.check(-1);
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());

        mDefaultRadioGroup.check(-3);
        assertEquals(-3, mDefaultRadioGroup.getCheckedRadioButtonId());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#clearCheck()}",
        method = "clearCheck",
        args = {}
    )
    @ToBeFixed(explanation = "Should not call OnCheckedChangeListener's method if "
            + "none of the inside buttons checked state is changed.")
    public void testClearCheck() {
        MockOnCheckedChangeListener listener = new MockOnCheckedChangeListener();
        mDefaultRadioGroup.setOnCheckedChangeListener(listener);

        mDefaultRadioGroup.check(BUTTON_ID_3);
        assertEquals(BUTTON_ID_3, mDefaultRadioGroup.getCheckedRadioButtonId());

        listener.reset();
        mDefaultRadioGroup.clearCheck();
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());
        assertHasCalledOnCheckedChanged(listener);
        // uncheck the original button
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, BUTTON_ID_3);

        // None of the buttons inside the group has of of the following IDs
        mDefaultRadioGroup.check(4);
        assertEquals(4, mDefaultRadioGroup.getCheckedRadioButtonId());

        listener.reset();
        mDefaultRadioGroup.clearCheck();
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());
        // why the method is called while none of the button is checked or unchecked?
        assertHasCalledOnCheckedChanged(listener);
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, -1);

        mDefaultRadioGroup.check(-1);
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());

        listener.reset();
        mDefaultRadioGroup.clearCheck();
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());
        // why the method is called while none of the button is checked or unchecked?
        assertHasCalledOnCheckedChanged(listener);
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, -1);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#check(int)}",
        method = "check",
        args = {int.class}
    )
    @ToBeFixed(explanation = "Should not call OnCheckedChangeListener's method if "
            + "none of the inside buttons checked state is changed.")
    public void testCheck() {
        MockOnCheckedChangeListener listener = new MockOnCheckedChangeListener();
        mDefaultRadioGroup.setOnCheckedChangeListener(listener);
        assertEquals(-1, mDefaultRadioGroup.getCheckedRadioButtonId());

        listener.reset();
        mDefaultRadioGroup.check(BUTTON_ID_0);
        assertHasCalledOnCheckedChanged(listener);
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, BUTTON_ID_0);

        listener.reset();
        mDefaultRadioGroup.check(BUTTON_ID_1);
        assertHasCalledOnCheckedChanged(listener);
        // uncheck the original button
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, BUTTON_ID_0);
        // check the new button
        assertOnCheckedChangedParams(listener, 1, mDefaultRadioGroup, BUTTON_ID_1);

        listener.reset();
        mDefaultRadioGroup.check(-1);
        assertHasCalledOnCheckedChanged(listener);
        // uncheck the original button
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, BUTTON_ID_1);
        assertOnCheckedChangedParams(listener, 1, mDefaultRadioGroup, -1);

        // None of the buttons inside the group has of of the following IDs
        listener.reset();
        mDefaultRadioGroup.check(-1);
        // why the method is called while none of the inside buttons has been changed
        assertHasCalledOnCheckedChanged(listener);
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, -1);

        listener.reset();
        mDefaultRadioGroup.check(4);
        // why the method is called while none of the inside buttons has been changed
        assertHasCalledOnCheckedChanged(listener);
        assertOnCheckedChangedParams(listener, 0, mDefaultRadioGroup, 4);

        // Set listener to null
        mDefaultRadioGroup.setOnCheckedChangeListener(null);
        // no exceptions thrown during the following method
        mDefaultRadioGroup.check(0);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#setOnCheckedChangeListener(OnCheckedChangeListener)}",
        method = "setOnCheckedChangeListener",
        args = {android.widget.RadioGroup.OnCheckedChangeListener.class}
    )
    public void testSetOnCheckedChangeListener() {
        MockOnCheckedChangeListener listener = new MockOnCheckedChangeListener();
        mDefaultRadioGroup.setOnCheckedChangeListener(listener);

        listener.reset();
        mDefaultRadioGroup.check(BUTTON_ID_0);
        assertHasCalledOnCheckedChanged(listener);

        // does not call the method if the button the id is already checked
        listener.reset();
        mDefaultRadioGroup.check(BUTTON_ID_0);
        assertHaveNotCalledOnCheckedChanged(listener);

        // call the method if none of the buttons inside the group has the id
        listener.reset();
        mDefaultRadioGroup.check(-3);
        assertHasCalledOnCheckedChanged(listener);

        // does not call the method if the button the id is already checked
        // and none of the buttons inside the group has the id
        listener.reset();
        mDefaultRadioGroup.check(-3);
        assertHaveNotCalledOnCheckedChanged(listener);

        // always call the method if the checked id is -1
        listener.reset();
        mDefaultRadioGroup.clearCheck();
        assertHasCalledOnCheckedChanged(listener);

        listener.reset();
        mDefaultRadioGroup.check(-1);
        assertHasCalledOnCheckedChanged(listener);

        // Set listener to null
        mDefaultRadioGroup.setOnCheckedChangeListener(null);
        // no exceptions thrown during the following method
        mDefaultRadioGroup.check(0);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#generateLayoutParams(AttributeSet)}",
        method = "generateLayoutParams",
        args = {android.util.AttributeSet.class}
    )
    public void testGenerateLayoutParams() {
        mDefaultRadioGroup = new RadioGroup(mContext);
        RadioGroup.LayoutParams layoutParams =
            mDefaultRadioGroup.generateLayoutParams((AttributeSet) null);
        assertNotNull(layoutParams);
        // default values
        assertEquals(0.0, layoutParams.weight, 0);
        assertEquals(-1, layoutParams.gravity);
        assertEquals(0, layoutParams.leftMargin);
        assertEquals(0, layoutParams.topMargin);
        assertEquals(0, layoutParams.rightMargin);
        assertEquals(0, layoutParams.bottomMargin);
        assertEquals(LayoutParams.WRAP_CONTENT, layoutParams.width);
        assertEquals(LayoutParams.WRAP_CONTENT, layoutParams.height);

        AttributeSet attrs = getAttributeSet(R.layout.radiogroup_1);
        layoutParams = mDefaultRadioGroup.generateLayoutParams(attrs);
        // values from layout
        assertNotNull(layoutParams);
        assertEquals(0.5, layoutParams.weight, 0);
        assertEquals(Gravity.BOTTOM, layoutParams.gravity);
        assertEquals(5, layoutParams.leftMargin);
        assertEquals(5, layoutParams.topMargin);
        assertEquals(5, layoutParams.rightMargin);
        assertEquals(5, layoutParams.bottomMargin);
        assertEquals(LayoutParams.FILL_PARENT, layoutParams.width);
        assertEquals(LayoutParams.FILL_PARENT, layoutParams.height);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#checkLayoutParams(android.view.ViewGroup.LayoutParams)}",
        method = "checkLayoutParams",
        args = {android.view.ViewGroup.LayoutParams.class}
    )
    public void testCheckLayoutParams() {
        MockRadioGroup mRadioGroupWrapper = new MockRadioGroup(mContext);

        assertFalse(mRadioGroupWrapper.checkLayoutParams(null));

        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        assertFalse(mRadioGroupWrapper.checkLayoutParams(relativeParams));

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        assertFalse(mRadioGroupWrapper.checkLayoutParams(linearParams));

        RadioGroup.LayoutParams radioParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.FILL_PARENT);
        assertTrue(mRadioGroupWrapper.checkLayoutParams(radioParams));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#generateDefaultLayoutParams()}",
        method = "generateDefaultLayoutParams",
        args = {}
    )
    public void testGenerateDefaultLayoutParams() {
        MockRadioGroup radioGroupWrapper = new MockRadioGroup(mContext);
        LinearLayout.LayoutParams p = radioGroupWrapper.generateDefaultLayoutParams();

        assertTrue(p instanceof RadioGroup.LayoutParams);
        assertEquals(RadioGroup.LayoutParams.WRAP_CONTENT, p.width);
        assertEquals(RadioGroup.LayoutParams.WRAP_CONTENT, p.height);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link RadioGroup#onFinishInflate()}",
        method = "onFinishInflate",
        args = {}
    )
    public void testOnFinishInflate() {
        MockRadioGroup radioGroup = new MockRadioGroup(mContext);
        int checkId = 100;
        radioGroup.check(checkId);
        // the button is added after the check(int)method
        // and it not checked though it has exactly the checkId
        RadioButton button = new RadioButton(mContext);
        button.setId(checkId);
        radioGroup.addView(button, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        MockOnCheckedChangeListener listener = new MockOnCheckedChangeListener();
        radioGroup.setOnCheckedChangeListener(listener);

        // check the button which id is CheckedRadioButtonId
        listener.reset();
        assertFalse(button.isChecked());
        radioGroup.onFinishInflate();
        assertTrue(button.isChecked());
        assertHasCalledOnCheckedChanged(listener);
        assertEquals(checkId, radioGroup.getCheckedRadioButtonId());

        radioGroup = new MockRadioGroup(mContext);
        button = new RadioButton(mContext);
        radioGroup.addView(button, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        listener = new MockOnCheckedChangeListener();
        radioGroup.setOnCheckedChangeListener(listener);

        // nothing happens if checkedRadioButtonId is -1
        assertEquals(-1, radioGroup.getCheckedRadioButtonId());
        assertFalse(button.isChecked());
        listener.reset();
        radioGroup.onFinishInflate();
        assertHaveNotCalledOnCheckedChanged(listener);
        assertEquals(-1, radioGroup.getCheckedRadioButtonId());
        assertFalse(button.isChecked());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "addView",
        args = {android.view.View.class, int.class, android.view.ViewGroup.LayoutParams.class}
    )
    public void testAddView() {
        mDefaultRadioGroup.check(BUTTON_ID_0);
        assertEquals(BUTTON_ID_0, mDefaultRadioGroup.getCheckedRadioButtonId());
        assertEquals(4, mDefaultRadioGroup.getChildCount());

        int id = BUTTON_ID_3 + 10;
        RadioButton choice4 = new RadioButton(mContext);
        choice4.setText("choice4");
        choice4.setId(id);
        choice4.setChecked(true);
        mDefaultRadioGroup.addView(choice4, 4, new ViewGroup.LayoutParams(100, 200));
        assertEquals(id, mDefaultRadioGroup.getCheckedRadioButtonId());
        assertEquals(5, mDefaultRadioGroup.getChildCount());
    }

    /**
     * Initialises the group with 4 RadioButtons which IDs are
     * BUTTON_ID_0, BUTTON_ID_1, BUTTON_ID_2, BUTTON_ID_3.
     */
    private RadioGroup createDefaultRadioGroup() {
        RadioGroup radioGroup = new RadioGroup(mContext);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        RadioButton choice0 = new RadioButton(mContext);
        choice0.setText("choice0");
        choice0.setId(BUTTON_ID_0);
        radioGroup.addView(choice0, params);

        RadioButton choice1 = new RadioButton(mContext);
        choice1.setText("choice1");
        choice1.setId(BUTTON_ID_1);
        radioGroup.addView(choice1, params);

        RadioButton choice2 = new RadioButton(mContext);
        choice2.setText("choice2");
        choice2.setId(BUTTON_ID_2);
        radioGroup.addView(choice2, params);

        RadioButton choice3 = new RadioButton(mContext);
        choice3.setText("choice3");
        choice3.setId(BUTTON_ID_3);
        radioGroup.addView(choice3, params);

        return radioGroup;
    }

    private AttributeSet getAttributeSet(int resId) {
        XmlPullParser parser = mContext.getResources().getLayout(resId);
        assertNotNull(parser);
        int type = 0;
        try {
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                // Empty
            }
        } catch (XmlPullParserException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertEquals("No RadioGroup element found", XmlPullParser.START_TAG, type);
        assertEquals("The first element is not RadioGroup", "RadioGroup", parser.getName());
        return Xml.asAttributeSet(parser);
    }

    private void assertHaveNotCalledOnCheckedChanged(MockOnCheckedChangeListener listener) {
        assertEquals(0, listener.getOnCheckedChangedGroupParams().size());
        assertEquals(0, listener.getOnCheckedChangedCheckedIdParams().size());
    }

    private void assertHasCalledOnCheckedChanged(MockOnCheckedChangeListener listener) {
        assertTrue(listener.getOnCheckedChangedGroupParams().size() > 0);
        assertTrue(listener.getOnCheckedChangedCheckedIdParams().size() > 0);
    }

    private void assertOnCheckedChangedParams(MockOnCheckedChangeListener listener, int time,
            RadioGroup paramGroup, int paramCheckedId) {
        assertSame(paramGroup,
                listener.getOnCheckedChangedGroupParams().get(time));
        assertEquals(paramCheckedId, listener
                .getOnCheckedChangedCheckedIdParams().get(time).intValue());
    }

    private class MockOnCheckedChangeListener implements OnCheckedChangeListener {
        private Vector<RadioGroup> mOnCheckedChangedGroupParams = new Vector<RadioGroup>();

        private Vector<Integer> mOnCheckedChangedCheckedIdParams = new Vector<Integer>();

        public Vector<RadioGroup> getOnCheckedChangedGroupParams() {
            return mOnCheckedChangedGroupParams;
        }

        public Vector<Integer> getOnCheckedChangedCheckedIdParams() {
            return mOnCheckedChangedCheckedIdParams;
        }

        public void reset() {
            mOnCheckedChangedGroupParams.clear();
            mOnCheckedChangedCheckedIdParams.clear();
        }

        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mOnCheckedChangedGroupParams.add(group);
            mOnCheckedChangedCheckedIdParams.add(checkedId);
        }
    }

    private class MockOnHierarchyChangeListener implements OnHierarchyChangeListener {
        private View mOnChildViewAddedParentParam;

        private View mOnChildViewAddedChildParam;

        private View mOnChildViewRemovedParentParam;

        private View mOnChildViewRemovedChildParam;

        public View getOnChildViewAddedParentParam() {
            return mOnChildViewAddedParentParam;
        }

        public View getOnChildViewAddedChildParam() {
            return mOnChildViewAddedChildParam;
        }

        public View getOnChildViewRemovedParentParam() {
            return mOnChildViewRemovedParentParam;
        }

        public View getOnChildViewRemovedChildParam() {
            return mOnChildViewRemovedChildParam;
        }

        public void reset() {
            mOnChildViewAddedParentParam = null;
            mOnChildViewAddedChildParam = null;
            mOnChildViewRemovedParentParam = null;
            mOnChildViewRemovedChildParam = null;
        }

        public void onChildViewAdded(View parent, View child) {
            mOnChildViewAddedParentParam = parent;
            mOnChildViewAddedChildParam = child;
        }

        public void onChildViewRemoved(View parent, View child) {
            mOnChildViewRemovedParentParam = parent;
            mOnChildViewRemovedChildParam = child;
        }
    }

    private class MockRadioGroup extends RadioGroup {
        public MockRadioGroup(Context context) {
            super(context);
        }

        @Override
        protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
            return super.checkLayoutParams(p);
        }

        @Override
        protected android.widget.LinearLayout.LayoutParams generateDefaultLayoutParams() {
            return super.generateDefaultLayoutParams();
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
        }
    }
}
