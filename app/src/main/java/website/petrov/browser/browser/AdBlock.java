package website.petrov.browser.browser;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import website.petrov.browser.database.RecordAction;
import website.petrov.browser.unit.BrowserUnit;

public class AdBlock {
    private static final String TAG = "AdBlock";

    private static final String FILE = "hosts.txt";
    private static final Set<String> hosts = new HashSet<>();
    private static final List<String> whitelist = new ArrayList<>();
    private final Context context;

    public AdBlock(@NonNull Context context) {
        this.context = context;

        if (hosts.isEmpty()) {
            loadHosts(context);
        }
        loadDomains(context);
    }

    private static void loadHosts(final Context context) {
        Thread thread = new Thread(() -> {
            AssetManager manager = context.getAssets();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(FILE)));
                String line;
                while ((line = reader.readLine()) != null) {
                    hosts.add(line.toLowerCase(Locale.getDefault()));
                }
            } catch (IOException i) {
                Log.e(TAG, "Error loading hosts", i);
            }
        });
        thread.start();
    }

    private synchronized static void loadDomains(@NonNull Context context) {
        RecordAction action = new RecordAction(context);
        action.open(false);
        whitelist.clear();
        whitelist.addAll(action.listDomains());
        action.close();
    }

    @NonNull
    private static String getDomain(@NonNull String url) {
        url = url.toLowerCase(Locale.getDefault());

        int index = url.indexOf('/', 8); // -> http://(7) and https://(8)
        if (index != -1) {
            url = url.substring(0, index);
        }

        String domain = BrowserUnit.safeGetHost(url);
        if (domain.isEmpty()) {
            return url;
        }
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public boolean isWhite(@NonNull String url) {
        for (String domain : whitelist) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    boolean isAd(@NonNull String url) {
        String domain = getDomain(url);
        return hosts.contains(domain.toLowerCase(Locale.getDefault()));
    }

    public synchronized void addDomain(@NonNull String domain) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.addDomain(domain);
        action.close();
        whitelist.add(domain);
    }

    public synchronized void removeDomain(@NonNull String domain) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.deleteDomain(domain);
        action.close();
        whitelist.remove(domain);
    }

    public synchronized void clearDomains() {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearDomains();
        action.close();
        whitelist.clear();
    }
}
