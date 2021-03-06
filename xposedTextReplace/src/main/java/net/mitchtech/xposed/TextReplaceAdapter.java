
package net.mitchtech.xposed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.mitchtech.xposed.textreplace.R;

import java.util.ArrayList;

public class TextReplaceAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<TextReplaceEntry> mData;

    public TextReplaceAdapter(Context context, ArrayList<TextReplaceEntry> data) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // this.resource = resource;
        this.mData = data;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // reuse a given view, or inflate a new one from the xml
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.replacement_row, parent, false);
        } else {
            view = convertView;
        }

        // bind the data to the view object
        return this.bindData(view, position);
    }

    public View bindData(View view, int position) {
        // make sure it's worth drawing the view
        if (mData.get(position) == null) {
            return view;
        }

        TextReplaceEntry item = mData.get(position);
        View viewElement = view.findViewById(R.id.actual);
        TextView tv = (TextView) viewElement;
        tv.setText(item.actual);

        viewElement = view.findViewById(R.id.replacement);
        tv = (TextView) viewElement;
        tv.setText(item.replacement);

        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView actual;
        TextView separator;
    }
}
