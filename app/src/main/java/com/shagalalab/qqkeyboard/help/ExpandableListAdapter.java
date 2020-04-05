package com.shagalalab.qqkeyboard.help;

import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.shagalalab.qqkeyboard.R;

import java.util.List;

class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private LayoutInflater layoutInflater;
    private List<Pair<String, CharSequence>> data;

    ExpandableListViewAdapter(LayoutInflater layoutInflater, List<Pair<String, CharSequence>> data) {
        this.layoutInflater = layoutInflater;
        this.data = data;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.data.get(groupPosition).first;
    }

    @Override
    public int getGroupCount() {
        return this.data.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_help_group, null);
        }

        TextView textViewGroup = convertView.findViewById(R.id.help_item_group);
        textViewGroup.setText(headerTitle);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.data.get(groupPosition).second;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final CharSequence childText = (CharSequence) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_help_child, null);
        }

        TextView textViewChild = convertView.findViewById(R.id.help_item_child);
        textViewChild.setText(childText);
        textViewChild.setMovementMethod(LinkMovementMethod.getInstance());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}