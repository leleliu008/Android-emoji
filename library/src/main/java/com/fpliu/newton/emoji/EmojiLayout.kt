package com.fpliu.newton.emoji

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.fpliu.newton.ui.list.ViewHolder
import com.fpliu.newton.ui.tab.ITab
import com.fpliu.newton.ui.tab.RelationShipAndPosition
import com.fpliu.newton.ui.tab.TabImpl
import com.shizhefei.view.indicator.IndicatorViewPager
import com.shizhefei.view.indicator.ScrollIndicatorView

class EmojiLayout : LinearLayout {

    private var tab: ITab<Int> = TabImpl()

    private var lastSelectedTabIndex = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    fun setup(editText: EditText) {
        setup(onEmojiSelected = {
            input(editText, it)
        }, onEmojiBackspaceClicked = {
            backspace(editText)
        })
    }

    fun setup(onEmojiSelected: (emoji: Emoji) -> Unit, onEmojiBackspaceClicked: () -> Unit) {
        setBackgroundColor(Color.WHITE)
        tab.apply {
            val context = context
            init(context, RelationShipAndPosition.LINEAR_BOTTOM, false).let {
                addView(it)
            }
            indicator = ScrollIndicatorView(context, null)
            setIndicatorWrapAndInCenter(Color.WHITE)
            setColorResScrollBar(R.color.background_head, 2)
            val items = arrayListOf(R.drawable.emoji_1f600, R.drawable.emoji_1f600, R.drawable.emoji_1f436, R.drawable.emoji_1f49d, R.drawable.emoji_1f307, R.drawable.emoji_1f523)
            viewPager.offscreenPageLimit = 6
            pagerAdapter = object : IndicatorViewPager.IndicatorViewPagerAdapter() {
                override fun getCount(): Int {
                    return items.size
                }

                override fun getViewForTab(position: Int, convertView: View?, container: ViewGroup): View {
                    val viewHolder = ViewHolder.getInstance(R.layout.emoji_type_tab_item, convertView, container)
                    viewHolder.id(R.id.emoji_type_tab_item_iv).image(items[position]).tag(position)
                    return viewHolder.getItemView()
                }

                override fun getViewForPage(position: Int, convertView: View?, container: ViewGroup): View {
                    //position与type是一一对应的关系
                    return EmojiGridView(context, position, onEmojiSelected)
                }
            }
            setOnIndicatorPageChangeListener { _, currentItem ->
                if (lastSelectedTabIndex == currentItem) {
                    return@setOnIndicatorPageChangeListener
                }
                lastSelectedTabIndex = currentItem
                RecentEmojiManager.getInstance(context).recentPage = currentItem
            }

            setItems(items)

            //删除键
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageResource(R.drawable.keyboard_delete)
                setOnClickListener { onEmojiBackspaceClicked.invoke() }
                setRightViewInIndicatorBar(this)
            }

            val recentEmojiManager = RecentEmojiManager.getInstance(context)
            var page = recentEmojiManager.recentPage
            // last page was recents, check if there are recents to use
            // if none was found, go to page 1
            if (page == 0 && recentEmojiManager.size == 0) {
                page = 1
            }

            setCurrentItem(page, false)
        }
    }

    fun input(editText: EditText, emoji: Emoji) {
        editText.run {
            val start = selectionStart
            val end = selectionEnd
            if (start < 0) {
                append(emoji.emoji)
            } else {
                text.replace(Math.min(start, end), Math.max(start, end), emoji.emoji, 0, emoji.emoji.length)
            }
        }
    }

    fun backspace(editText: EditText) {
        val event = KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL)
        editText.dispatchKeyEvent(event)
    }
}
