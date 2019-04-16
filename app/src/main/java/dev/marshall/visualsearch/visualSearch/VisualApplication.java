package dev.marshall.visualsearch.visualSearch;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatDelegate;

public class VisualApplication extends Application {
    private static VisualApplication instance;
    private static Context appContext;

    public static VisualApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}