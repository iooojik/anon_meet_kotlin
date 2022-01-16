package iooojik.anon.meet;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Objects;

import iooojik.anon.meet.shared.prefs.SharedPreferencesManager;
import iooojik.anon.meet.shared.prefs.SharedPrefsKeys;

public class Application extends android.app.Application {
    /* private static Application applicationInstance;/
     public static synchronized Application getInstance() {
        return applicationInstance;
    }
    */

    public static void setTheme(@NonNull Context context) {
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(context);
        preferencesManager.initPreferences("iooojik.anon.meet.preferences", Context.MODE_PRIVATE);
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(Objects.requireNonNull(preferencesManager.getValue(SharedPrefsKeys.THEME_MODE, AppCompatDelegate.MODE_NIGHT_NO)).toString()));

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //applicationInstance = this;
        setTheme(this);
        AdUtil.Companion.initializeAd(this);
    }

}
