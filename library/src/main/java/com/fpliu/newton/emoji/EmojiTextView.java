package com.fpliu.newton.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;

public class EmojiTextView extends AppCompatTextView {

    private int emojiSize;
    private int emojiAlignment;
    private int emojiTextSize;
    private int mTextStart = 0;
    private int mTextLength = -1;
    private boolean mUseSystemDefault = false;

    public EmojiTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        emojiTextSize = (int) getTextSize();
        if (attrs == null) {
            emojiSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emoji);
            emojiSize = (int) a.getDimension(R.styleable.Emoji_emojiSize, getTextSize());
            emojiAlignment = a.getInt(R.styleable.Emoji_emojiAlignment, DynamicDrawableSpan.ALIGN_BASELINE);
            mTextStart = a.getInteger(R.styleable.Emoji_emojiTextStart, 0);
            mTextLength = a.getInteger(R.styleable.Emoji_emojiTextLength, -1);
            mUseSystemDefault = a.getBoolean(R.styleable.Emoji_emojiUseSystemDefault, false);
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            EmojiHandler.addEmojis(getContext(), builder, emojiSize, emojiAlignment, emojiTextSize, mTextStart, mTextLength, mUseSystemDefault);
            text = builder;
        }
        super.setText(text, type);
    }

    public void setEmojiSize(int pixels) {
        emojiSize = pixels;
        super.setText(getText());
    }

    public void setUseSystemDefault(boolean useSystemDefault) {
        mUseSystemDefault = useSystemDefault;
    }
}
