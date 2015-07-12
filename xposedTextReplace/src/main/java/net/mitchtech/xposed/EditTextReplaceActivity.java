
package net.mitchtech.xposed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.tsengvn.typekit.TypekitContextWrapper;

import net.mitchtech.xposed.textreplace.R;

import java.util.ArrayList;

public class EditTextReplaceActivity extends AppCompatActivity {

    private static final String TAG = EditTextReplaceActivity.class.getSimpleName();
    private static final String PKG_NAME = "net.mitchtech.xposed.textreplace";

    private ListView mListview;
    private TextView mListEmptyTextView;
    private ArrayList<TextReplaceEntry> mAliasList;
    private TextReplaceAdapter mAliasAdapter;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_replacements);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mListview = (ListView) findViewById(R.id.listview);
        mListEmptyTextView = (TextView) findViewById(R.id.listEmptyText);
        mAliasList = MacroUtils.loadMacroList(mPrefs);

        mAliasAdapter = new TextReplaceAdapter(this, mAliasList);
        mListview.setAdapter(mAliasAdapter);
        mListview.setTextFilterEnabled(true);
        mListview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editReplacement((TextReplaceEntry) parent.getItemAtPosition(position), position);
            }
        });

        mListview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeReplacement(position);
                return true;
            }
        });

        Drawable plus = new IconicsDrawable(this, FontAwesome.Icon.faw_plus).color(Color.WHITE).sizeDp(20);
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add);
        addButton.setImageDrawable(plus);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReplacement();
            }
        });
    }

    @Override
    protected void onResume() {
        if (mAliasList == null || mAliasList.isEmpty()) {
            mListEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mListEmptyTextView.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add:
                addReplacement();
                return true;

            case R.id.action_settings:
                Intent settings = new Intent(this, TextReplacePreferenceActivity.class);
                startActivity(settings);
                return true;

            case R.id.action_exit:
                this.finish();
                return true;
        }

        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    private void addReplacement() {
        editReplacement(new TextReplaceEntry("", ""), -1);
    }

    private void editReplacement(TextReplaceEntry entry, final int position) {
        LayoutInflater factory = LayoutInflater.from(EditTextReplaceActivity.this);
        final View textEntryView = factory.inflate(R.layout.dialog_edit_replacement, null);
        final EditText actual = (EditText) textEntryView.findViewById(R.id.actual);
        final EditText replacement = (EditText) textEntryView.findViewById(R.id.replacement);
        actual.setText(entry.actual, TextView.BufferType.EDITABLE);
        replacement.setText(entry.replacement, TextView.BufferType.EDITABLE);

        new MaterialDialog.Builder(EditTextReplaceActivity.this)
                .title("Define Macro")
                .customView(textEntryView, true)
                .positiveText("Save")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String actualText = actual.getText().toString();
                        String replacementText = replacement.getText().toString();
                        // if (isTextRegexFree(actualText)
                        if (position > -1) {
                            mAliasList.remove(mListview.getItemAtPosition(position));
                        }
                        mAliasList.add(new TextReplaceEntry(actualText, replacementText));
                        mListEmptyTextView.setVisibility(View.GONE);
                        mAliasAdapter.notifyDataSetChanged();
                        MacroUtils.saveMacroList(mAliasList, mPrefs);
                    }
                }).show();
    }

    private void removeReplacement(final int position) {
        new MaterialDialog.Builder(EditTextReplaceActivity.this)
                .title("Delete Macro?")
                .content("Are you sure you want to delete this replacement?")
                .positiveText("Confirm")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        mAliasList.remove(mListview.getItemAtPosition(position));
                        if (mAliasList.isEmpty()) {
                            mListEmptyTextView.setVisibility(View.VISIBLE);
                        }
                        mAliasAdapter.notifyDataSetChanged();
                        MacroUtils.saveMacroList(mAliasList, mPrefs);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        // no action
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {

                    }
                }).show();
    }

    private void macroListLengthWarningDialog(int length) {
        new MaterialDialog.Builder(EditTextReplaceActivity.this)
                .title("Size Warning")
                .content("Waring, macro list contains " + length
                        + " entries. This is permitted, but performance my degrade as a result")
                .positiveText("OK")
                .show();
    }
}
