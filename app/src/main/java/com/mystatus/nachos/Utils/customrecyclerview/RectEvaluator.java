/*
 * Copyright 2015 Vsevolod Ganin
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

package com.mystatus.nachos.Utils.customrecyclerview;

import android.animation.TypeEvaluator;
import android.graphics.Rect;

import androidx.annotation.NonNull;


/**
 * Simple {@link Rect} evaluator. Made because of minimum SDK which doesn't contain
 * implementation.
 */
class RectEvaluator implements TypeEvaluator<Rect> {

    private final Rect mCacheRect = new Rect();

    @NonNull
    @Override
    public Rect evaluate(float fraction, @NonNull Rect startValue, @NonNull Rect endValue) {
        int left = startValue.left + (int) ((endValue.left - startValue.left) * fraction);
        int top = startValue.top + (int) ((endValue.top - startValue.top) * fraction);
        int right = startValue.right + (int) ((endValue.right - startValue.right) * fraction);
        int bottom = startValue.bottom + (int) ((endValue.bottom - startValue.bottom) * fraction);

        mCacheRect.set(left, top, right, bottom);
        return mCacheRect;
    }
}
