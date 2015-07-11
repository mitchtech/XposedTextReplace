
package net.mitchtech.xposed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

        final AlertDialog.Builder alert = new AlertDialog.Builder(EditTextReplaceActivity.this);
        alert.setIcon(R.drawable.ic_launcher).setTitle("Define Replacement").setView(textEntryView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        
                        String actualText = actual.getText().toString();
                        String replacementText = replacement.getText().toString();
                        
                        // if (isTextRegexFree(actualText) &&
                        // isTextRegexFree(replacementText)) {
                        if (position > -1) {
                            mAliasList.remove(mListview.getItemAtPosition(position));
                        }
                        mAliasList.add(new TextReplaceEntry(actualText, replacementText));
                        mListEmptyTextView.setVisibility(View.GONE);
                        mAliasAdapter.notifyDataSetChanged();
                        MacroUtils.saveMacroList(mAliasList, mPrefs);
                        // } else {
                        // Toast.makeText(
                        // EditTextReplaceActivity.this,
                        // "Alias cannot contain regular expression characters ($, ^, +, *, ., !, ?, |, \\, (), {}, [])",
                        // Toast.LENGTH_SHORT).show();
                        // editReplacement(new TextReplaceEntry(actualText,
                        // replacementText), position);
                        // }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        alert.show();
    }

    private void removeReplacement(final int position) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(EditTextReplaceActivity.this);
        alert.setIcon(R.drawable.ic_launcher).setTitle("Delete Replacement?")
                .setMessage("Are you sure you want to delete this replacement?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mAliasList.remove(mListview.getItemAtPosition(position));
                        if (mAliasList.isEmpty()) {
                            mListEmptyTextView.setVisibility(View.VISIBLE);
                        }
                        mAliasAdapter.notifyDataSetChanged();
                        MacroUtils.saveMacroList(mAliasList, mPrefs);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        alert.show();
    }


}
