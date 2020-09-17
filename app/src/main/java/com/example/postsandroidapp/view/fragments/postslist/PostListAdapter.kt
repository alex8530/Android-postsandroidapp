package com.example.postsandroidapp.view.fragments.postslist

import AdapterCallback
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.postsandroidapp.R
import com.example.postsandroidapp.databinding.LayoutListItemPostBinding
import com.example.postsandroidapp.model.classes.Post
import com.example.postsandroidapp.model.utils.JavaUtils

class PostListAdapter<Model>(
    val context: Context,
    var mListModel: MutableList<Model>,
    val myItemCallback: AdapterCallback

) : RecyclerView.Adapter<PostListAdapter<Model>.PostViewHolder>() {


    companion object {
        var Tag: String = PostListAdapter::class.java.simpleName
        var AddToCartTag: String = PostListAdapter::class.java.simpleName + "AddToCartTag"
        var AddToWishTag: String = PostListAdapter::class.java.simpleName + "AddToWishTag"
    }

    var positionSelected = -1
        set(value) {
            notifyItemChanged(positionSelected)
            field = value
            notifyItemChanged(positionSelected)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {

        val binding: LayoutListItemPostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_list_item_post,
            parent,
            false
        )

        return PostViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return mListModel.size
    }

    fun add(item: Model?) {
        if (item == null) {
            return
        }
        val lastItemIndex: Int = this.mListModel.size
        this.mListModel.add(item)
        notifyItemInserted(lastItemIndex)
        notifyDataSetChanged()
    }

    fun setAnimation(recyclerView: RecyclerView) {
        JavaUtils.setupAnimationChangeDataOnRecyclerView(
            context
            , recyclerView
            , this
            , R.anim.layout_animation_down_to_up
        )
    }

    fun addAll(arrayListItem: MutableList<Model>?) {
        if (arrayListItem == null || arrayListItem.isEmpty()) {
            return
        }
        val lastItemIndex: Int = mListModel.size
        mListModel.addAll(arrayListItem)
        notifyItemRangeInserted(lastItemIndex, arrayListItem.size)
    }

    fun clear() {
        mListModel.clear()
        notifyDataSetChanged()
    }

    fun getItem(item: Model?) {
        mListModel.find { model -> item == model }
    }

    fun getItem(position: Int): Model? {
        if (mListModel.isNullOrEmpty()) return null
        return mListModel[position]
    }

    fun removeItem(position: Int) {

        mListModel.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mListModel.size)
    }

    fun removeItem(item: Model) {

        val positionItem = mListModel.indexOf(item)
        if (positionItem!=-1){
            mListModel.removeAt(positionItem)
            notifyItemRemoved(positionItem)
            notifyItemRangeChanged(positionItem, mListModel.size)
        }

    }


    inner class PostViewHolder(private var binding: LayoutListItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int) {

            val model = mListModel[position] as Post
            binding.postTitleTextView.text = model.title


            binding.checkAppCompatCheckBox.isChecked = model.isSelected!!


            binding.checkAppCompatCheckBox.setOnClickListener {
                (mListModel[position] as Post).isSelected = binding.checkAppCompatCheckBox.isChecked
            }


            binding.rootCoordinatorLayout.setOnClickListener {
                myItemCallback.onClickAdapterItem(
                    adapterPosition,
                    Tag,model
                )
            }
        }
    }

}