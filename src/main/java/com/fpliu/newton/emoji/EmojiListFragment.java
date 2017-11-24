package com.fpliu.newton.emoji;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fpliu.newton.log.Logger;
import com.fpliu.newton.ui.base.BaseView;
import com.fpliu.newton.ui.list.RecyclerViewFragment;
import com.fpliu.newton.ui.recyclerview.holder.ItemViewHolder;
import com.fpliu.newton.ui.recyclerview.layout.HorizontalPageGridLayoutManager;
import com.fpliu.newton.ui.recyclerview.layout.PagingScrollHelper;

import java.util.Arrays;
import java.util.List;

/**
 * 某个分类的列表
 */
public class EmojiListFragment extends RecyclerViewFragment<Emoji, ItemViewHolder> {

    private static final String TAG = EmojiListFragment.class.getSimpleName();

    private static final String KEY_TYPE = "type";

    private int type;

    private int page;

    private LinearLayout footer;

    private OnEmojiSelectedListener onEmojiSelectedListener;

    public static EmojiListFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);

        EmojiListFragment fragment = new EmojiListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EmojiListFragment newInstance(int type, OnEmojiSelectedListener onEmojiSelectedListener) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);

        EmojiListFragment fragment = new EmojiListFragment();
        fragment.setArguments(args);
        fragment.onEmojiSelectedListener = onEmojiSelectedListener;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TYPE, type);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args == null) {
                type = Emoji.TYPE_UNDEFINED;
            } else {
                type = args.getInt(KEY_TYPE, Emoji.TYPE_UNDEFINED);
            }
        } else {
            type = savedInstanceState.getInt(KEY_TYPE, Emoji.TYPE_UNDEFINED);
        }
    }

    @Override
    protected void onCreateViewLazy(BaseView baseView, Bundle savedInstanceState) {
        super.onCreateViewLazy(baseView, savedInstanceState);

        Context context = getActivity();

        int rows = 3;
        int columns = 9;

        List<Emoji> items;
        if (type == Emoji.TYPE_UNDEFINED) {
            items = RecentEmojiManager.getInstance(context);
        } else {
            items = Arrays.asList(Emoji.getEmojis(type));
        }

        footer = new LinearLayout(context);
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setBackgroundColor(getResources().getColor(R.color.white));
        footer.setPadding(0, 20, 0, 5);
        footer.setGravity(Gravity.CENTER);
        setViewAfterBody(footer);

        if (items != null) {
            int size = items.size();
            if (size > 0) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = 5;
                lp.rightMargin = 5;
                lp.gravity = Gravity.CENTER;

                int page = rows * columns;
                int xx = size / page;
                if (size % page != 0) {
                    ++xx;
                }

                for (int i = 0; i < xx; i++) {
                    ImageView imageView = new ImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setImageResource(R.drawable.indicator_unselect_emoji);
                    if (i == 0) {
                        imageView.setImageResource(R.drawable.indicator_selected_emoji);
                    }
                    footer.addView(imageView, lp);
                }
            }
        }

        HorizontalPageGridLayoutManager layoutManager = new HorizontalPageGridLayoutManager(3, 9);
        PagingScrollHelper pagingScrollHelper = new PagingScrollHelper();
        pagingScrollHelper.setUpRecycleView(getRecyclerView());
        pagingScrollHelper.setOnPageChangeListener(page -> {
            Logger.i(TAG, "onPageChange() page = " + page);
            View view = footer.getChildAt(EmojiListFragment.this.page);
            if (view != null) {
                ImageView imageView = (ImageView) view;
                imageView.setImageResource(R.drawable.indicator_unselect_emoji);
            }

            view = footer.getChildAt(page);
            if (view != null) {
                ImageView imageView = (ImageView) view;
                imageView.setImageResource(R.drawable.indicator_selected_emoji);
            }
            EmojiListFragment.this.page = page;
        });
        setLayoutManager(layoutManager);
        pagingScrollHelper.updateLayoutManger();

        setItems(items);

        getRecyclerView().setBackgroundColor(Color.WHITE);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return ItemViewHolder.newInstance(R.layout.emoji_list_item, viewGroup);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position, Emoji emoji) {
        holder.id(R.id.emoji_list_item_emoji_tv).text(emoji.getEmoji());
    }

    @Override
    public void onItemClick(ItemViewHolder holder, int position, Emoji item) {
        super.onItemClick(holder, position, item);
        if (onEmojiSelectedListener != null) {
            onEmojiSelectedListener.onEmojiSelected(item);
        }
        RecentEmojiManager.getInstance(getActivity()).add(item);
    }
}
