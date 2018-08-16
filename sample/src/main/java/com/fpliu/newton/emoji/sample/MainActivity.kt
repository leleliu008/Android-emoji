package com.fpliu.newton.emoji.sample

import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.fpliu.newton.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Emoji键盘使用示例
 * @author 792793182@qq.com 2018-03-28.
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Emoji键盘使用示例"
        setContentView(R.layout.activity_main)
        emojiLayout.setup(emojiEditText, {
            showToast("发送：${emojiEditText.text}")
        })
        emojiLayout.setBackgroundColor(Color.parseColor("#f8f8f8"))
        editorActions(emojiEditText).subscribe {
            if (it == EditorInfo.IME_ACTION_SEND) {
                showToast("发送：${emojiEditText.text}")
            }
        }
    }
}