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

package android.text.style.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.SuperscriptSpan;

import junit.framework.TestCase;

@TestTargetClass(CharacterStyle.class)
public class CharacterStyleTest extends TestCase {
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link CharacterStyle#wrap(CharacterStyle)}",
        method = "wrap",
        args = {android.text.style.CharacterStyle.class}
    )
    public void testWrap() {
        // use a MetricAffectingSpan
        MetricAffectingSpan metricAffectingSpan = new SuperscriptSpan();

        CharacterStyle result = CharacterStyle.wrap(metricAffectingSpan);
        assertNotNull(result);
        assertSame(metricAffectingSpan, result.getUnderlying());
        assertNotSame(metricAffectingSpan, result);

        // use a no-MetricAffectingSpan
        CharacterStyle characterStyle = new MyCharacterStyle();
        result = CharacterStyle.wrap(characterStyle);
        assertNotNull(result);
        assertTrue(result instanceof CharacterStyle);
        assertSame(characterStyle, result.getUnderlying());
        assertNotSame(characterStyle, result);

        result = CharacterStyle.wrap((MetricAffectingSpan) null);
        assertNotNull(result);
        assertTrue(result instanceof CharacterStyle);

        result = CharacterStyle.wrap((CharacterStyle) null);
        assertNotNull(result);
        assertTrue(result instanceof CharacterStyle);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Test {@link CharacterStyle#getUnderlying()}",
        method = "getUnderlying",
        args = {}
    )
    public void testGetUnderlying() {
        CharacterStyle expected = new MyCharacterStyle();
        assertSame(expected, expected.getUnderlying());

        MetricAffectingSpan metricAffectingSpan = new SuperscriptSpan();
        CharacterStyle result = CharacterStyle.wrap(metricAffectingSpan);
        assertNotNull(result);
        assertTrue(result instanceof MetricAffectingSpan);
        assertSame(metricAffectingSpan, result.getUnderlying());
    }

    /**
     * MyCharacterStyle for test.
     */
    private class MyCharacterStyle extends CharacterStyle {
        @Override
        public void updateDrawState(TextPaint tp) {
            // implement abstract method.
        }
    }
}
