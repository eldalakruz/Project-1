package com.example.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.Model.Post
import com.example.myapplication.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context,
                  private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()

{
    private var firebaseUser: FirebaseUser? = null

    inner class  ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView
        var campaign_btn: Button


        var pollquestion : TextView
        var contestantone : TextView
        var contestanttwo : TextView



        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage =  itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
            campaign_btn = itemView.findViewById(R.id.campaign_btn)

            pollquestion = itemView.findViewById(R.id.tv_question)
            contestantone = itemView.findViewById(R.id.tv_option1)
            contestanttwo = itemView.findViewById(R.id.tv_option2)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

       // val modelVideo = videoArrayList!![position]

        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        //description
        if (post.getDescription().equals(""))
        {
            holder.description.visibility = View.GONE

        }
        else
        {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

//        // 1
//        if (post.getTvoption1().equals(""))
//        {
//            holder.contestantone.visibility = View.GONE
//
//        }
//        else
//        {
//            holder.contestantone.visibility = View.VISIBLE
//            holder.contestantone.setText(post.getTvoption1())
//        }
//
//        //2
//        if (post.getTvoption2().equals(""))
//        {
//            holder.contestanttwo.visibility = View.GONE
//
//        }
//        else
//        {
//            holder.contestanttwo.visibility = View.VISIBLE
//            holder.contestanttwo.setText(post.getTvoption2())
//        }
//
//        //3
//        if (post.getTvquestion().equals(""))
//        {
//            holder.pollquestion.visibility = View.GONE
//
//        }
//        else
//        {
//            holder.pollquestion.visibility = View.VISIBLE
//            holder.pollquestion.setText(post.getTvquestion())
//        }

        //


        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)


        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()


                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

        holder.commentButton.setOnClickListener {

            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.campaign_btn.setOnClickListener {
            val intent = Intent(mContext, CompaignPage::class.java)
            intent.putExtra("postId", post.getPostid())
            intent.putExtra("publisherId", post.getPublisher())
            intent.putExtra("contestantone", post.getContestantone())
            Log.e("sample","test")

            intent.putExtra("contestanttwo",post.getContestanttwo())

            Log.e("sample","test")

            mContext.startActivity(intent)

        }


        holder.comments.setOnClickListener {

            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }


        holder.saveButton.setOnClickListener {
            if (holder.saveButton.tag == "Save")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves").child(firebaseUser!!.uid)
                    .child(post.getPostid()).setValue(true)
            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Saves").child(firebaseUser!!.uid)
                    .child(post.getPostid()).removeValue()
            }

        }


    }


    private fun numberOfLikes(likes: TextView, postid: String)
    {
     val LikesRef = FirebaseDatabase.getInstance().reference
         .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists())
                {
                    likes.text = pO.childrenCount.toString() + " likes"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun getTotalComments(comments: TextView, postid: String)
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists())
                {
                    comments.text = "view all " + pO.childrenCount.toString() + " comments"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }






    private fun isLikes(postid: String, likeButton: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.child(firebaseUser!!.uid).exists())
                {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                }
                else
                {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        userRef.addValueEventListener(object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
               if (snapshot.exists())
               {
                   val user = snapshot.getValue<User>(User::class.java)

                   Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                   userName.text = user!!.getUsername()
                   publisher.text = user!!.getFullname()

               }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun checkSavedStatus(postid: String, imageView: ImageView)
    {
        val saveRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        saveRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(postid).exists())
                {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag = "Saved"
                }
                else
                {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag = "Save"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}












