package com.fpliu.newton.emoji

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.fpliu.newton.ui.base.UIUtil
import com.fpliu.newton.ui.list.ViewHolder
import com.fpliu.newton.ui.tab.ITab
import com.fpliu.newton.ui.tab.RelationShipAndPosition
import com.fpliu.newton.ui.tab.TabImpl
import com.shizhefei.view.indicator.IndicatorViewPager
import com.shizhefei.view.indicator.ScrollIndicatorView

class EmojiLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private val tab: ITab<Int> by lazy { TabImpl<Int>() }

    private var lastSelectedTabIndex = 0

    fun setup(editText: EditText,
              onEmojiSendClicked: (() -> Unit)? = null, //如果是null，表示不展示发送按钮
              @ColorInt selectedTabIndicatorBackgroundColor: Int = Color.parseColor("#f8f8f8")) {
        setup(onEmojiSelected = {
            performInput(editText, it)
        }, onEmojiBackspaceClicked = {
            performDelete(editText)
        }, onEmojiSendClicked = onEmojiSendClicked, selectedTabIndicatorBackgroundColor = selectedTabIndicatorBackgroundColor)
    }

    fun setup(onEmojiSelected: (Emoji) -> Unit,
              onEmojiBackspaceClicked: () -> Unit,
              onEmojiSendClicked: (() -> Unit)? = null, //如果是null，表示不展示发送按钮
              @ColorInt selectedTabIndicatorBackgroundColor: Int = Color.parseColor("#f8f8f8")) {
        tab.apply {
            val context = context

            //添加TabView
            addView(init(context, RelationShipAndPosition.LINEAR_BOTTOM, false))

            indicator = ScrollIndicatorView(context, null)
            setIndicatorWrapAndAlignLeft(Color.WHITE)
            setOnTransitionBackgroundColorChange(selectedTabIndicatorBackgroundColor, Color.TRANSPARENT)
            val items = arrayListOf(R.drawable.emoji_1f600, R.drawable.emoji_1f600, R.drawable.emoji_1f436, R.drawable.emoji_1f49d, R.drawable.emoji_1f307, R.drawable.emoji_1f523)
            viewPager.offscreenPageLimit = 6
            pagerAdapter = object : IndicatorViewPager.IndicatorViewPagerAdapter() {
                override fun getCount() = items.size

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

            //如果是null，表示不展示发送按钮
            if (onEmojiSendClicked == null) {
                //删除键
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setImageResource(R.drawable.keyboard_delete)
                    setOnClickListener { onEmojiBackspaceClicked.invoke() }
                }.let {
                    setRightViewInIndicatorBar(it)
                }
            } else {
                val rightView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER

                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT)
                    setRightViewInIndicatorBar(this, lp)
                }

                //删除键
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setImageResource(R.drawable.keyboard_delete)
                    setOnClickListener { onEmojiBackspaceClicked.invoke() }
                }.let {
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    lp.rightMargin = UIUtil.dp2px(context, 30)
                    rightView.addView(it, lp)
                }

                //发送键
                TextView(context).apply {
                    text = "发送"
                    textSize = 13f
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.color.blue)
                    setOnClickListener { onEmojiSendClicked.invoke() }
                    setPadding(UIUtil.dp2px(context, 10), UIUtil.dp2px(context, 5), UIUtil.dp2px(context, 10), UIUtil.dp2px(context, 5))
                }.let { rightView.addView(it) }
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

    fun performInput(editText: EditText, emoji: Emoji) {
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

    fun performDelete(editText: EditText) {
        val event = KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL)
        editText.dispatchKeyEvent(event)
    }
}
