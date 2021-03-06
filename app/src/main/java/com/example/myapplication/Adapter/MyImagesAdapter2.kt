package com.example.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Post
import com.example.myapplication.PostDetailsActivity
import com.example.myapplication.R
import com.squareup.picasso.Picasso

class MyImagesAdapter2 (private val mContext : Context, mPost : List<Post>)
: RecyclerView.Adapter<MyImagesAdapter2.ViewHolder?>() {

    private var mPost: List<Post>? = null

    init {
        this.mPost = mPost
    }

    inner class ViewHolder(@NonNull itemView: View)
        : RecyclerView.ViewHolder(itemView)
    {
        val postImage : ImageView

        init {
            postImage = itemView.findViewById(R.id.post_image)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post : Post = mPost!![position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        holder.postImage.setOnClickListener {
               val intent = Intent(mContext, PostDetailsActivity::class.java)
               intent.putExtra("publisherId", post.getPostid())
               mContext.startActivity(intent)
           }
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

}

