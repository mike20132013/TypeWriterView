package com.trevore.animationtesting;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by Trevor on 4/27/2014.
 */
public class TypeWriterView extends FrameLayout {

    //The underlying EditText
    private EditText editText;

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

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            editText.setText(text.subSequence(0, textIndex));
            editText.setSelection(textIndex);
            textIndex++;
            if(textIndex <= text.length()) {
                handler.postDelayed(characterAdder, delay);
                if(callback != null)
                    callback.onCharacterTyped(text.charAt(textIndex));
            }
            else {
                finished = true;
                if(callback != null)
                    callback.onAnimationEnd();
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
        editText = new EditText(context);
        addView(editText);

        //Remove text suggestions for completion and spelling
        editText.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        //Enable cursor
        editText.setCursorVisible(true);

        //Remove the blue underline in >= ICS
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editText.setBackgroundDrawable(null);
        } else {
            editText.setBackground(null);
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
        editText.getText().clear();

        //Remove any existing Runnable and add our new one
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    /**
     * Use this sparingly to make specific tweaks you need.  Wrapper methods will be provided for common tasks.
     * @return
     */
    public EditText getEditText() {
        return editText;
    }

    /**
     * Checks if the current animation has finished or not.
     * @return
     */
    public boolean isFinishedAnimating() {
        return finished;
    }

    /**
     * Must also be sure to set the textCursorDrawable via your style or XML.
     * @param color
     */
    public void setTextColor(ColorStateList color) {
        editText.setTextColor(color);
    }

    /**
     * Must also be sure to set the textCursorDrawable via your style or XML.
     * @param color
     */
    public void setTextColor(int color) {
        editText.setTextColor(color);
    }

    /**
     * Sets the delay between characters being typed.
     * @param millis
     */
    public void setCharacterDelay(long millis) {
        delay = millis;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
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