package net.mitchtech.xposed;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

public class TextReplaceApplication extends Application {

    private static String TAG = TextReplaceApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "Roboto-Light.ttf"))
                .addBold(Typekit.createFromAsset(this, "Roboto-Regular.ttf"))
                .addItalic(Typekit.createFromAsset(this, "Roboto-LightItalic.ttf"))
                .addBoldItalic(Typekit.createFromAsset(this, "Roboto-BoldItalic.ttf"));
    }
}