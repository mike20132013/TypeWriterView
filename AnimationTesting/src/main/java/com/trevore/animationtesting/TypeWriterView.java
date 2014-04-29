package com.trevore.animationtesting;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Copyright 2014 Trevor Elkins

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
public class TypeWriterView extends EditText {

    //The text to be animated
    private CharSequence text;

    //The index of the most recent added character
    private int textIndex;

    //Delay between displaying characters
    private long delay = 200;

    //Handler to post delayed events with
    private Handler handler = new Handler();

    //Whether the animation has finished or not
    private boolean finished = true;

    //The Callback to post events to
    private Callback callback;

    //Runnable that recursively adds characters to the edittext and performs callbacks
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            //Set the text to our subsequence
            setText(text.subSequence(0, textIndex));
            setSelection(textIndex);

            //If the index is in our bounds then output current char
            if(callback != null && text.length() > 0 && textIndex < text.length()) {
                callback.onCharacterTyped(text.charAt(textIndex));
            }

            //this gets tricky.  we need to send one more pass through to update the final character
            textIndex++;
            if(textIndex <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
            else {
                finished = true;
                if(callback != null) {
                    callback.onAnimationEnd();
                }
            }
        }
    };


    public TypeWriterView(Context context) {
        super(context);
        init(context);
    }

    public TypeWriterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TypeWriterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        //Override the default touch listener
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Remove text suggestions for completion and spelling
        setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        //Enable cursor
        setCursorVisible(true);

        //Remove the blue underline in >= ICS
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(null);
        } else {
            setBackground(null);
        }
    }

    /**
     * Animates the text using the delay value set.
     * @param text
     */
    public void animateText(CharSequence text) {
        this.text = text;
        textIndex = 0;
        finished = false;
        getText().clear();

        //Request focus start blinking again
        requestFocus();

        //Remove any existing Runnable and add our new one
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    /**
     * Checks if the current animation has finished or not.
     * @return
     */
    public boolean isFinishedAnimating() {
        return finished;
    }

    /**
     * Sets the delay between characters being typed.
     * @param millis
     */
    public void setCharacterDelay(long millis) {
        delay = millis;
    }

    /**
     * Sets the Callback to be used.
     * @param callback
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        public void onAnimationEnd();
        public void onCharacterTyped(char character);
    }

}