package website.petrov.browser.view;

import androidx.annotation.Nullable;

public class GridItem {
    @Nullable
    private String title;
    @Nullable
    private String url;
    @Nullable
    private String filename;
    private int ordinal;

    public GridItem() {
        this.title = null;
        this.url = null;
        this.filename = null;
        this.ordinal = -1;
    }

    public GridItem(@Nullable String title, @Nullable String url, @Nullable String filename, int ordinal) {
        this.title = title;
        this.url = url;
        this.filename = filename;
        this.ordinal = ordinal;
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

    public void setURL(@Nullable String url) {
        this.url = url;
    }

    @Nullable
    public String getFilename() {
        return filename;
    }

    public void setFilename(@Nullable String filename) {
        this.filename = filename;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}
