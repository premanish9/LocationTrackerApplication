package com.example.locationtrackerapplication.view

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.locationtrackerapplication.R
import com.example.locationtrackerapplication.databinding.RidePassItemBinding
import com.example.locationtrackerapplication.model.Article

/**
 * @author Prem
 */
class NewsListViewHolder(
    private val activity: Activity?,
    private val activityViewModel: MainActivityViewModel?,
    private val binding: RidePassItemBinding,
    private val mCallback: MainClickCallback
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo(itemPaymentType: Article,isClick:Boolean) {







        binding.tvnewsTitle.text=itemPaymentType.title
        binding.tvCredit.text=itemPaymentType.publishedAt


        Glide.with(activity!!).load(itemPaymentType.urlToImage)
            .apply(
                RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    //  .placeholder(R.drawable.ic_user_placeholder)
                    .skipMemoryCache(true)
            )
            .into(binding.imgnews)


        Glide.with(activity!!).load(R.drawable.filled_circle)
            .apply(
                RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    //  .placeholder(R.drawable.ic_user_placeholder)
                    .skipMemoryCache(true)
            )
            .into(binding.imgblueicon)



        itemView.setOnClickListener {
            if (isClick){
                mCallback.onPaymentSelected(itemPaymentType,activity)
            }
        }
    }
}