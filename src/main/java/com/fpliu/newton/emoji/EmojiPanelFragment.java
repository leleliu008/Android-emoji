package com.fpliu.newton.emoji;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fpliu.newton.ui.base.BaseView;
import com.fpliu.newton.ui.list.ViewHolder;
import com.fpliu.newton.ui.tab.RelationShipAndPosition;
import com.fpliu.newton.ui.tab.TabFragmentFragment;
import com.shizhefei.view.indicator.ScrollIndicatorView;

import java.util.Arrays;

public class EmojiPanelFragment extends TabFragmentFragment<Integer> {

    private OnEmojiSelectedListener onEmojiSelectedListener;
    private OnEmojiBackspaceClickedListener mOnEmojiBackspaceClickedListener;
    private int mEmojiTabLastSelectedIndex = 0;
    private boolean mUseSystemDefault = false;

    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";

    public static EmojiPanelFragment newInstance(boolean useSystemDefault) {
        EmojiPanelFragment fragment = new EmojiPanelFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUseSystemDefault = getArguments().getBoolean(USE_SYSTEM_DEFAULT_KEY);
        } else {
            mUseSystemDefault = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof OnEmojiBackspaceClickedListener) {
            onEmojiSelectedListener = (OnEmojiSelectedListener) getActivity();
            mOnEmojiBackspaceClickedListener = (OnEmojiBackspaceClickedListener) getActivity();
        } else if (getParentFragment() instanceof OnEmojiBackspaceClickedListener) {
            onEmojiSelectedListener = (OnEmojiSelectedListener) getActivity();
            mOnEmojiBackspaceClickedListener = (OnEmojiBackspaceClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(context + " must implement interface " + OnEmojiBackspaceClickedListener.class.getSimpleName() +
                    " and " + OnEmojiSelectedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        onEmojiSelectedListener = null;
        mOnEmojiBackspaceClickedListener = null;
        super.onDetach();
    }

    @Override
    protected void onCreateViewLazy(BaseView baseView, Bundle savedInstanceState) {
        super.onCreateViewLazy(baseView, savedInstanceState);

        setCanScroll(false);
        setIndicator(new ScrollIndicatorView(getActivity(), null));
        setIndicatorWrapAndInCenter(Color.TRANSPARENT);
        getContentView().setBackgroundColor(getResources().getColor(R.color.white));
        setColorResScrollBar(R.color.background_head, 2);
        setItems(Arrays.asList(new Integer[]{R.drawable.emoji_1f600,
                R.drawable.emoji_1f64f,
                R.drawable.emoji_1f3a9,
                R.drawable.emoji_1f340,
                R.drawable.emoji_1f49d,
                R.drawable.emoji_1f3af}));
        getViewPager().setOffscreenPageLimit(6);
        //删除键
        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.keyboard_delete);
        imageView.setOnTouchListener(new RepeatListener(1000, 50, v -> {
            if (mOnEmojiBackspaceClickedListener != null) {
                mOnEmojiBackspaceClickedListener.onEmojiBackspaceClicked(v);
            }
        }));
        setRightViewInIndicatorBar(imageView);

        RecentEmojiManager recentEmojiManager = RecentEmojiManager.getInstance(getActivity());
        int page = recentEmojiManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && recentEmojiManager.size() == 0) {
            page = 1;
        }

        setCurrentItem(page, false);
    }

    @Override
    public View getViewForTab(int position, View view, ViewGroup viewGroup, Integer item) {
        ViewHolder viewHolder = ViewHolder.getInstance(R.layout.emoji_type_tab_item, view, viewGroup);
        viewHolder.id(R.id.emoji_type_tab_item_iv).image(item).tag(position);
        return viewHolder.getItemView();
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        //position与type是一一对应的关系
        return EmojiListFragment.newInstance(position, onEmojiSelectedListener);
    }

    @Override
    public void onIndicatorPageChange(int preItem, int currentItem) {
        super.onIndicatorPageChange(preItem, currentItem);
        if (mEmojiTabLastSelectedIndex == currentItem) {
            return;
        }
        mEmojiTabLastSelectedIndex = currentItem;
        RecentEmojiManager.getInstance(getActivity()).setRecentPage(currentItem);
    }

    @Override
    public RelationShipAndPosition getRelationShipAndPosition() {
        return RelationShipAndPosition.LINEAR_BOTTOM;
    }

    public static void input(EditText editText, Emoji emoji) {
        if (editText == null || emoji == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emoji.getEmoji());
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), emoji.getEmoji(), 0, emoji.getEmoji().length());
        }
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }


    /**
     * <p>
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * </p>
     * <p>
     * Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     * </p>
     */
    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }
            return false;
        }
    }
}
