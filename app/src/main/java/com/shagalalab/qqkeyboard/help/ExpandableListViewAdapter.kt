package com.shagalalab.qqkeyboard.help

import android.text.method.LinkMovementMethod
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.shagalalab.qqkeyboard.R

class ExpandableListViewAdapter(private val layoutInflater: LayoutInflater, private val data: List<Pair<String, CharSequence>>): BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return data[groupPosition].first
    }

    override fun getGroupCount(): Int {
        return data.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View? {
        val newConvertView  = convertView ?: layoutInflater.inflate(R.layout.item_help_group, null)
        val headerTitle = getGroup(groupPosition) as String

        if (newConvertView != null){
            val textViewGroup = newConvertView.findViewById<TextView>(R.id.help_item_group)
            textViewGroup.text = headerTitle
        }

        return newConvertView
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return data[groupPosition].second
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View? {

        val newConvertView = convertView ?: layoutInflater.inflate(R.layout.item_help_child, null)
        val childText = getChild(groupPosition, childPosition) as CharSequence

        if (newConvertView != null){
            val textViewChild = newConvertView.findViewById<TextView>(R.id.help_item_child)
            textViewChild.text = childText
            textViewChild.movementMethod = LinkMovementMethod.getInstance()
        }

        return newConvertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}