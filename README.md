# Android-emoji
一个<code>emoji</code>库，基于<a href="https://github.com/rockerhieu/emojicon" target="_blank">https://github.com/rockerhieu/emojicon</a>进行修改，使用<code>Kotlin</code>语言重新实现

## 效果图
![](effect.jpg =445x774)

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
如果您的界面中只在一个<code>EditText</code>里使用这个<code>EmojiLayout</code>，您可以直接调用另外一个方法：
```
emojiLayout.setup(emojiEditText)
```

## 注意
要展示<code>emoji</cpde>，必须使用<code>com.fpliu.newton.emoji.EmojiTextView</code>或者<code>com.fpliu.newton.emoji.EmojiEditText</code>，普通的<code>View</code>无法展示。
具体使用方法可查看app模块。
