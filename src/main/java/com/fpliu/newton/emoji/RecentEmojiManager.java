package com.fpliu.newton.emoji;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class RecentEmojiManager extends ArrayList<Emoji> {

    private static final String DELIMITER = ",";

    private static final String PREFERENCE_NAME = "emoji";

    private static final String PREF_RECENTS = "recent_emojis";

    private static final String PREF_PAGE = "recent_page";

    private static final Object LOCK = new Object();

    private static RecentEmojiManager sInstance;

    private static int maximumSize = 40;

    private Context mContext;

    private RecentEmojiManager(Context context) {
        mContext = context.getApplicationContext();
        loadRecents();
    }

    public static RecentEmojiManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new RecentEmojiManager(context);
                }
            }
        }
        return sInstance;
    }

    public int getRecentPage() {
        return getPreferences().getInt(PREF_PAGE, 0);
    }

    public void setRecentPage(int page) {
        getPreferences().edit().putInt(PREF_PAGE, page).commit();
    }

    public void push(Emoji object) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(object)) {
            super.remove(object);
        }
        add(0, object);
    }

    @Override
    public boolean add(Emoji object) {
        boolean ret = super.add(object);

        while (this.size() > RecentEmojiManager.maximumSize) {
            super.remove(0);
        }

        saveRecents();
        return ret;
    }

    @Override
    public void add(int index, Emoji object) {
        super.add(index, object);

        if (index == 0) {
            while (this.size() > RecentEmojiManager.maximumSize) {
                super.remove(RecentEmojiManager.maximumSize);
            }
        } else {
            while (this.size() > RecentEmojiManager.maximumSize) {
                super.remove(0);
            }
        }

        saveRecents();
    }

    @Override
    public boolean remove(Object object) {
        boolean ret = super.remove(object);
        saveRecents();
        return ret;
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private void loadRecents() {
        SharedPreferences prefs = getPreferences();
        String str = prefs.getString(PREF_RECENTS, "");
        StringTokenizer tokenizer = new StringTokenizer(str, RecentEmojiManager.DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            add(Emoji.fromChars(tokenizer.nextToken()));
        }
    }

    private void saveRecents() {
        StringBuilder str = new StringBuilder();
        int c = size();
        for (int i = 0; i < c; i++) {
            Emoji e = get(i);
            str.append(e.getEmoji());
            if (i < (c - 1)) {
                str.append(RecentEmojiManager.DELIMITER);
            }
        }
        SharedPreferences prefs = getPreferences();
        prefs.edit().putString(PREF_RECENTS, str.toString()).commit();
    }

    public static void setMaximumSize(int maximumSize) {
        RecentEmojiManager.maximumSize = maximumSize;
    }
}
