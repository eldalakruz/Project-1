package com.example.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.Post
import com.example.myapplication.PostDetailsActivity
import com.example.myapplication.R
import com.example.myapplication.fragments.NewPostDetailsFragment
import com.example.myapplication.fragments.PostDetailsFragment
import com.squareup.picasso.Picasso

class MyImagesAdapter2 (private val mContext : Context, mPost : List<Newpost>)
: RecyclerView.Adapter<MyImagesAdapter2.ViewHolder?>() {

    private var mPost: List<Newpost>? = null

    init {
        this.mPost = mPost
    }

    inner class ViewHolder(@NonNull itemView: View)
        : RecyclerView.ViewHolder(itemView)
    {
        val postImage : ImageView = itemView.findViewById(R.id.post_image)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post : Newpost = mPost!![position]

        if (post.getPostimage().isEmpty()) {
            holder.postImage.setImageResource(R.drawable.profile)
        } else {
            Picasso.get().load(post.getPostimage()).into(holder.postImage)
        }

        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.getPostid())
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, NewPostDetailsFragment()).commit()
        }

//        holder.postImage.setOnClickListener {
//               val intent = Intent(mContext, PostDetailsActivity::class.java)
//               intent.putExtra("publisherId", post.getPostid())
//               mContext.startActivity(intent)
//           }
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

}

