package com.fpliu.newton.emoji;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.text.style.DynamicDrawableSpan;

import com.fpliu.newton.emoji.type.Nature;
import com.fpliu.newton.emoji.type.Objects;
import com.fpliu.newton.emoji.type.People;
import com.fpliu.newton.emoji.type.Places;
import com.fpliu.newton.emoji.type.Symbols;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Emoji implements Parcelable {

    @IntDef({DynamicDrawableSpan.ALIGN_BASELINE, DynamicDrawableSpan.ALIGN_BOTTOM})
    public @interface Alignment {
    }

    @IntDef({TYPE_UNDEFINED, TYPE_PEOPLE, TYPE_NATURE, TYPE_OBJECTS, TYPE_PLACES, TYPE_SYMBOLS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_PEOPLE = 1;
    public static final int TYPE_NATURE = 2;
    public static final int TYPE_OBJECTS = 3;
    public static final int TYPE_PLACES = 4;
    public static final int TYPE_SYMBOLS = 5;

    public static Emoji[] getEmojis(@Type int type) {
        switch (type) {
            case TYPE_PEOPLE:
                return People.DATA;
            case TYPE_NATURE:
                return Nature.DATA;
            case TYPE_OBJECTS:
                return Objects.DATA;
            case TYPE_PLACES:
                return Places.DATA;
            case TYPE_SYMBOLS:
                return Symbols.DATA;
            default:
                return null;
        }
    }

    public static final Creator<Emoji> CREATOR = new Creator<Emoji>() {
        @Override
        public Emoji createFromParcel(Parcel in) {
            return new Emoji(in);
        }

        @Override
        public Emoji[] newArray(int size) {
            return new Emoji[size];
        }
    };

    private int icon;

    private char value;

    private String emoji;

    public Emoji(int icon, char value, String emoji) {
        this.icon = icon;
        this.value = value;
        this.emoji = emoji;
    }

    public Emoji(Parcel in) {
        this.icon = in.readInt();
        this.value = (char) in.readInt();
        this.emoji = in.readString();
    }

    private Emoji() {
    }

    public Emoji(String emoji) {
        this.emoji = emoji;
    }

    public static Emoji fromResource(int icon, int value) {
        Emoji emoji = new Emoji();
        emoji.icon = icon;
        emoji.value = (char) value;
        return emoji;
    }

    public static Emoji fromCodePoint(int codePoint) {
        Emoji emoji = new Emoji();
        emoji.emoji = newString(codePoint);
        return emoji;
    }

    public static Emoji fromChar(char ch) {
        Emoji emoji = new Emoji();
        emoji.emoji = Character.toString(ch);
        return emoji;
    }

    public static Emoji fromChars(String chars) {
        Emoji emoji = new Emoji();
        emoji.emoji = chars;
        return emoji;
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeInt(value);
        dest.writeString(emoji);
    }

    public char getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Emoji && emoji.equals(((Emoji) o).emoji);
    }

    @Override
    public int hashCode() {
        return emoji.hashCode();
    }

}
