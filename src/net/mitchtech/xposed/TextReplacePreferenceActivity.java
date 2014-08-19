
package net.mitchtech.xposed;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import net.mitchtech.xposed.textreplace.R;

public class TextReplacePreferenceActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
