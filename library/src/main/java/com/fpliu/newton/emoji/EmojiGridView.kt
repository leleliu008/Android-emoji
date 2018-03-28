package com.fpliu.newton.emoji

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.recyclerview.adapter.ItemAdapter
import com.fpliu.newton.ui.recyclerview.holder.ItemViewHolder
import com.fpliu.newton.ui.recyclerview.layout.HorizontalPageGridLayoutManager
import com.fpliu.newton.ui.recyclerview.layout.PagingScrollHelper
import java.util.*

/**
 * 某个分类的列表
 */
class EmojiGridView(context: Context, type: Int = Emoji.TYPE_UNDEFINED, private val onEmojiSelected: ((emoji: Emoji) -> Unit)? = null) : LinearLayout(context), View.OnClickListener {

    companion object {
        private val TAG = EmojiGridView::class.java.simpleName
    }

    private var page: Int = 0

    init {
        orientation = VERTICAL
        when (type) {
            Emoji.TYPE_UNDEFINED -> RecentEmojiManager.getInstance(context)
            else -> Arrays.asList(*Emoji.getEmojis(type)!!)
        }?.run {
            val size = size
            if (size > 0) {
                val recyclerView = RecyclerView(context).apply {
                    setBackgroundColor(Color.WHITE)
                }
                addView(recyclerView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.resources.getDimension(R.dimen.dp750_300).toInt()))

                val footerView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                    setBackgroundColor(Color.WHITE)
                    setPadding(0, 20, 0, 5)
                }
                addView(footerView)

                PagingScrollHelper().apply {
                    setUpRecycleView(recyclerView)
                    setOnPageChangeListener {
                        Logger.i(TAG, "onPageChange() page = $it")
                        (footerView.getChildAt(page) as? ImageView)?.setImageResource(R.drawable.indicator_unselect_emoji)
                        (footerView.getChildAt(it) as? ImageView)?.setImageResource(R.drawable.indicator_selected_emoji)
                        page = it
                    }
                    recyclerView.layoutManager = HorizontalPageGridLayoutManager(3, 9).apply {
                        isAutoMeasureEnabled = true
                    }
                    updateLayoutManger()
                }

                recyclerView.adapter = object : ItemAdapter<Emoji, ItemViewHolder>(this) {
                    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ItemViewHolder {
                        return ItemViewHolder.newInstance(R.layout.emoji_list_item, viewGroup)
                    }

                    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, emoji: Emoji) {
                        holder.id(R.id.emoji_list_item_emoji_tv).text(emoji.emoji).tagWithCurrentId(emoji).clicked(this@EmojiGridView)
                    }
                }

                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    leftMargin = 5
                    rightMargin = 5
                    gravity = Gravity.CENTER
                }

                val rows = 3
                val columns = 9
                val page = rows * columns
                var xx = size / page
                if (size % page != 0) {
                    ++xx
                }

                (0 until xx)
                    .map {
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_INSIDE
                            when (it) {
                                0 -> R.drawable.indicator_selected_emoji
                                else -> R.drawable.indicator_unselect_emoji
                            }.let { setImageResource(it) }
                        }
                    }
                    .forEach { footerView.addView(it, lp) }
            }
        }
    }

    override fun onClick(v: View) {
        (v.getTag(R.id.emoji_list_item_emoji_tv) as? Emoji)?.run {
            onEmojiSelected?.invoke(this)
            RecentEmojiManager.getInstance(context).add(this)
        }
    }
}
