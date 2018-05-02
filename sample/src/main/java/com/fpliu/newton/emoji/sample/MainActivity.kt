package com.fpliu.newton.emoji.sample

import android.os.Bundle
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
        emojiLayout.setup(emojiEditText)
    }
}