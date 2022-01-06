package iooojik.anon.meet;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Objects;

import iooojik.anon.meet.shared.prefs.SharedPreferencesManager;
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys;

public class Application extends android.app.Application {
    private static Application applicationInstance;

    public static synchronized Application getInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
        setTheme(this);
        AdUtil.Companion.initializeAd(this);
    }

    public static void setTheme(Context context) {
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(context);
        preferencesManager.initPreferences("iooojik.anon.meet.preferences", Context.MODE_PRIVATE);
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt((String)
                Objects.requireNonNull(preferencesManager.getValue(SharedPrefsKeys.THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO)).toString()));

    }

}
