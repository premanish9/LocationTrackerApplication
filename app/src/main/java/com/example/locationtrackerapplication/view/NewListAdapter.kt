package com.example.locationtrackerapplication.view

import com.example.locationtrackerapplication.model.Article


import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationtrackerapplication.R
import com.example.locationtrackerapplication.databinding.RidePassItemBinding

/**
 * @author Prem
 */
class NewListAdapter(
    private val activity: Activity?,
    private val activityViewModel: MainActivityViewModel?,
    private val mEntries: ArrayList<Article>?,
    private val mCallback: MainClickCallback
) : RecyclerView.Adapter<NewsListViewHolder>() {

    override fun getItemCount(): Int {
        return mEntries?.size ?: 0
    }

    var isClick=true;

    fun changeClick(clicked:Boolean){
        isClick=clicked
        notifyDataSetChanged()
    }

    fun updateList(list: ArrayList<Article>){
        mEntries?.clear()
        mEntries?.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_main, parent, false) // Replace with your item layout

        val binding = RidePassItemBinding.inflate(layoutInflater, parent, false)
        return NewsListViewHolder(activity, activityViewModel,binding, mCallback)
    }

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        val item = mEntries?.get(position)
        item?.let {

            holder.bindTo(it,isClick)


        }
    }

}