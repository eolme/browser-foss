package de.baumann.browser.Browser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.LinkedList;
import java.util.List;

import de.baumann.browser.View.NinjaWebView;

public class BrowserContainer {
    private static final List<AlbumController> list = new LinkedList<>();

    @Contract(pure = true)
    @NonNull
    public static AlbumController get(int index) {
        return list.get(index);
    }

    public synchronized static void set(@Nullable AlbumController controller, int index) {
        if (list.get(index) instanceof NinjaWebView) {
            ((NinjaWebView) list.get(index)).destroy();
        }
        list.set(index, controller);
    }

    public synchronized static void add(@Nullable AlbumController controller) {
        list.add(controller);
    }

    public synchronized static void add(@Nullable AlbumController controller, int index) {
        list.add(index, controller);
    }

    public synchronized static void remove(@Nullable AlbumController controller) {
        if (controller instanceof NinjaWebView) {
            ((NinjaWebView) controller).destroy();
        }
        list.remove(controller);
    }

    public static int indexOf(@Nullable AlbumController controller) {
        return list.indexOf(controller);
    }

    @Contract(pure = true)
    @NonNull
    public static List<AlbumController> list() {
        return list;
    }

    public static int size() {
        return list.size();
    }

    public synchronized static void clear() {
        for (AlbumController albumController : list) {
            if (albumController instanceof NinjaWebView) {
                ((NinjaWebView) albumController).destroy();
            }
        }
        list.clear();
    }
}
