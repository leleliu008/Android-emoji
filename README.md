# Android-emoji
一个emoji库，基于<a href="https://github.com/rockerhieu/emojicon" target="_blank">https://github.com/rockerhieu/emojicon</a>，进行修改，使用Kotlin语言重新实现

## 效果图
![](effect.jpg)

## 使用方式
1、布局：
```
<com.fpliu.newton.emoji.EmojiLayout
    android:id="@+id/emojiLayout"
    android:layout_width="match_parent"
    android:layout_height="@dimen/dp750_395" />
```
2、注册事件（Kotlin语言版）：
```
emojiLayout.apply {
    setup(onEmojiSelected = {
        input(emojiEditText, it)
    }, onEmojiBackspaceClicked = {
        backspace(emojiEditText)
    }) 
}
```
如果您的界面中只在一个EditText里使用这个EmojiLayout，您可以直接调用另外一个方法：
```
emojiLayout.setup(emojiEditText)
```

## 注意
要展示emoji，必须使用emojiTextView或者EmojiEditText，普通的View无法展示。
具体使用方法可查看app模块。
