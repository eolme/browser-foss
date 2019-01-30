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

public class Javascript {
    private static final String TAG = "FragmentSettingsData";

    private static final String FILE = "javaHosts.txt";
    private static final Set<String> hostsJS = new HashSet<>();
    private static final List<String> whitelistJS = new ArrayList<>();
    @NonNull
    private final Context context;

    public Javascript(@NonNull Context context) {
        this.context = context;

        if (hostsJS.isEmpty()) {
            loadHosts(context);
        }
        loadDomains(context);
    }

    private static void loadHosts(@NonNull Context context) {
        Thread thread = new Thread(() -> {
            AssetManager manager = context.getAssets();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(FILE)));
                String line;
                while ((line = reader.readLine()) != null) {
                    hostsJS.add(line.toLowerCase(Locale.getDefault()));
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
        whitelistJS.clear();
        whitelistJS.addAll(action.listDomainsJS());
        action.close();
    }

    public boolean isWhite(@NonNull String url) {
        for (String domain : whitelistJS) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addDomain(@NonNull String domain) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.addDomainJS(domain);
        action.close();
        whitelistJS.add(domain);
    }

    public synchronized void removeDomain(@NonNull String domain) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.deleteDomainJS(domain);
        action.close();
        whitelistJS.remove(domain);
    }

    public synchronized void clearDomains() {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearDomainsJS();
        action.close();
        whitelistJS.clear();
    }
}
