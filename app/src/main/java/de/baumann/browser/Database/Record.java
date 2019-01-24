package de.baumann.browser.Database;

import androidx.annotation.Nullable;

public class Record {
    @Nullable
    private String title;
    @Nullable
    private String url;

    public Record(@Nullable String title, @Nullable String url, long time) {
        this.title = title;
        this.url = url;
        this.time = time;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    public String getURL() {
        return url;
    }

    private long time;
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public Record() {
        this.title = null;
        this.url = null;
        this.time = 0L;
    }

    public void setURL(@Nullable String url) {
        this.url = url;
    }
}
