# Android-emoji
一个<code>emoji</code>库，基于<a href="https://github.com/rockerhieu/emojicon" target="_blank">https://github.com/rockerhieu/emojicon</a>进行修改，使用<code>Kotlin</code>语言重新实现

## 效果图
<img src="./effect.jpg" width="445" height="774" alt="效果图" />

## 引用
```
api("com.fpliu:Android-emoji:1.0.0")
```

## com.fpliu.newton.emoji.EmojiTextView
该控件用来展示<code>emoji</code>的，继承自<code>TextView</code><br>
使用方法如下：<br>
1、布局：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:emoji="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.fpliu.newton.emoji.EmojiTextView
        android:id="@+id/emojiEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        emoji:emojiSize="@dimen/sp750_40"/>

    <!-- -->
</LinearLayout>
```
当然，也可以通过代码创建和调用该类的方法。

## com.fpliu.newton.emoji.EmojiEditText
该控件用来输入<code>emoji</code>的，继承自<code>EditText</code><br>
使用方法如下：<br>
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:emoji="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.fpliu.newton.emoji.EmojiEditText
        android:id="@+id/emojiEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="请输入文字"
        emoji:emojiSize="@dimen/sp750_40"/>
    
    <!-- -->
</LinearLayout>
```
当然，也可以通过代码创建和调用该类的方法。

## com.fpliu.newton.emoji.EmojiLayout
使用方法如下：<br>
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
如果您的界面中只在一个<code>com.fpliu.newton.emoji.EmojiEditText</code>里使用这个<code>EmojiLayout</code>，您可以直接调用另外一个方法：
```
emojiLayout.setup(emojiEditText)
```

## 注意
要展示<code>emoji</code>，必须使用<code>com.fpliu.newton.emoji.EmojiTextView</code>，普通的<code>TextView</code>无法展示出美观的<code>emoji</code>。<br>
要输入<code>emoji</code>，必须使用<code>com.fpliu.newton.emoji.EmojiEditText</code>，普通的<code>EditText</code>无法输入美观的<code>emoji</code>。<br>
详细的使用方法可查看app模块的示例。
