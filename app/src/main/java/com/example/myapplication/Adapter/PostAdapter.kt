package com.example.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CommentsActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.Model.Post
import com.example.myapplication.Model.User
import com.example.myapplication.R
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

        var pollquestion : TextView
        var contestantone : TextView
        var contestanttwo : TextView

         var seekBar1 : SeekBar
         var seekBar2 : SeekBar
         var tvPercent1 : TextView
         var tvPercent2 : TextView




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

            pollquestion = itemView.findViewById(R.id.tv_question)
            contestantone = itemView.findViewById(R.id.tv_option1)
            contestanttwo = itemView.findViewById(R.id.tv_option2)

            seekBar1 = itemView.findViewById(R.id.seek_bar1)
            seekBar2 = itemView.findViewById(R.id.seek_bar2)
            tvPercent1 = itemView.findViewById(R.id.tv_percent1)
            tvPercent2 = itemView.findViewById(R.id.tv_percent2)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return  ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

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

        // 1
       if (post.getPollquestion().equals(""))
       {
           holder.pollquestion.visibility = View.GONE

        }
       else
      {
            holder.pollquestion.visibility = View.VISIBLE
            holder.pollquestion.setText(post.getPollquestion())
       }

        //2
        if (post.getContestantone().equals(""))
        {
           holder.contestantone.visibility = View.GONE

        }
        else
        {
            holder.contestantone.visibility = View.VISIBLE
            holder.contestantone.setText(post.getContestantone())
        }

        //3
        if (post.getContestanttwo().equals(""))
        {
            holder.contestanttwo.visibility = View.GONE

        }
        else
        {
            holder.contestanttwo.visibility = View.VISIBLE
            holder.contestanttwo.setText(post.getContestanttwo())
        }

        //
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)
        calculatePecent(holder.tvPercent1, holder.tvPercent2)

        //

//        holder.seekBar1.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(v: View?) {Log.e(TAG,"seekbar 1")
       // })
        holder.contestantone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                var count1 = 1
                var count2 = 1
                var flag1 = true
                var flag2 = true

                Log.e(TAG,"seekbar 1 clicked")

                if (flag2)
                {
                    // when flag two is true
                    count1 = 1
                    count2++
                    flag1 = true
                    flag2 = false

                    // calculate percentage
                    calculatePecent(holder.tvPercent1, holder.tvPercent2)
                }
            }
        })

        holder.seekBar2.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean { return  true }
        })

        holder.contestanttwo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                var count1 = 1
                var count2 = 1
                var flag1 = true
                var flag2 = true

                if (flag1)
                {
                    // when flag two is true
                    count1++
                    count2 = 1

                    flag1 = false
                    flag2 = true

                    // calculate percentage
                    calculatePecent(holder.tvPercent1, holder.tvPercent2)
                }
            }
        })

        //

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


    private fun calculatePecent(tvPercent1 : TextView, tvPercent2 : TextView) {

        var count1 = 1
        var count2 = 1
        // calculate total
        val total = (count1 + count2).toDouble()

        // Calculate percentage for all options
        val percent1 = count1 / total * 100
        val percent2 = count2 / total * 100

        // set percent on text view
        tvPercent1.text = String.format("%.0f%%", percent1)
        // Set progress on seekbar
         // seekBar1.progress = percent1.toInt()

        tvPercent2.text = String.format("%.0f%%", percent2)
        //   seekBar2.progress = percent2.toInt()

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




