package com.fpliu.newton.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;

public class EmojiMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {

    private int emojiSize;
    private int emojiAlignment;
    private int emojiTextSize;
    private boolean mUseSystemDefault = false;

    public EmojiMultiAutoCompleteTextView(Context context) {
        super(context);
        emojiSize = (int) getTextSize();
        emojiTextSize = (int) getTextSize();
    }

    public EmojiMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiMultiAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emoji);
        emojiSize = (int) a.getDimension(R.styleable.Emoji_emojiSize, getTextSize());
        emojiAlignment = a.getInt(R.styleable.Emoji_emojiAlignment, DynamicDrawableSpan.ALIGN_BASELINE);
        mUseSystemDefault = a.getBoolean(R.styleable.Emoji_emojiUseSystemDefault, false);
        a.recycle();
        emojiTextSize = (int) getTextSize();
        setText(getText());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        updateText();
    }

    public void setEmojiSize(int pixels) {
        emojiSize = pixels;
        updateText();
    }

    public void setUseSystemDefault(boolean useSystemDefault) {
        mUseSystemDefault = useSystemDefault;
    }

    private void updateText() {
        EmojiHandler.addEmojis(getContext(), getText(), emojiSize, emojiAlignment, emojiTextSize, mUseSystemDefault);
    }
}
