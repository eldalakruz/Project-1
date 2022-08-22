package com.example.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CompaignComments2
import com.example.myapplication.Model.ModelVideo2
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
import java.util.*
import kotlin.collections.ArrayList


class AdapterVideo2 (
    private var context2: Context,
    private var videoArrayList2 : ArrayList<ModelVideo2>
) : RecyclerView.Adapter<AdapterVideo2.HolderVideo>() {

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVideo2.HolderVideo {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        //inflate layout row_video.xml
        val view = LayoutInflater.from(context2).inflate(R.layout.row_video2, parent, false)
        return AdapterVideo2.HolderVideo(view)
    }

    override fun onBindViewHolder(holder: AdapterVideo2.HolderVideo, position: Int) {
        /*-----get data, set data, handle clicks etc-----*/

        //get data
        val modelVideo2 = videoArrayList2!![position]

        //get speific data
        val id2: String? = modelVideo2.id2
        val title2: String? = modelVideo2.title2
        val timestamp2: String? = modelVideo2.timestamp2
        val videoUri2: String? = modelVideo2.videoUri2

        //format date e.g. 16/03/2022 11:38am
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp2!!.toLong()
        val formattedDateTime = android.text.format.DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString()

        //set data
        holder.titleTv2.text = title2
        holder.timeTv2.text = formattedDateTime
        setVideoUrl(modelVideo2, holder)

        getUserInfo(holder.userimage, holder.username, modelVideo2.getPublisher())
        isLikes(modelVideo2.id2!!, holder.like_btn2)
        numberOfLikes(holder.like_count2, modelVideo2.id2!!)
        getTotalComments(holder.comment_count2, modelVideo2.id2)


        holder.like_btn2.setOnClickListener {
            if (holder.like_btn2.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(modelVideo2.id2!!)
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }

            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(modelVideo2.id2!!)
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }
        }


        holder.comment_btn2.setOnClickListener {
            val intent = Intent(context2, CompaignComments2::class.java)
            intent.putExtra("id2" , modelVideo2.id2)
            intent.putExtra("publisher" , modelVideo2.getPublisher())
            context2.startActivity(intent)
        }

    }

    private fun numberOfLikes(likeCount2: TextView, id2: String)
    {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(id2)

        LikesRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    likeCount2.text = snapshot.childrenCount.toString() + "likes"
                }
                else
                {
                    likeCount2.text = snapshot.childrenCount.toString() + "likes"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun isLikes(publisher: String, likeBtn2: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(publisher)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists())
                {
                    likeBtn2.setImageResource(R.drawable.heart_clicked)
                    likeBtn2.tag = "Liked"
                }
                else
                {
                    likeBtn2.setImageResource(R.drawable.heart_not_click)
                    likeBtn2.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getTotalComments(commentBtn2: TextView, id2: String?)
    {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("CompaignComments2").child(id2!!)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    commentBtn2.text ="view all " + snapshot.childrenCount.toString() + " comments"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getUserInfo(userimage: CircleImageView, username: TextView, publisher: String)
    {
        val userRef = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue(User::class.java)


                    if (user!!.getImage().isEmpty()) {
                        userimage.setImageResource(R.drawable.profile)
                    } else {
                        Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(userimage)
                    }


  //                  Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_person_black).into(userimage)

                    username.text = user!!.getUsername()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setVideoUrl(modelVideo: ModelVideo2, holder: HolderVideo) {
        //show progress
        holder.progressBar2.visibility = View.VISIBLE

        //get video uri
        val videoUrl: String? = modelVideo.videoUri2

        //MediaController for play/pause/time etc
        val mediaController = MediaController(context2)
        mediaController.setAnchorView(holder.videoView2)
        val videoUri = Uri.parse(videoUrl)

        holder.videoView2.setMediaController(mediaController)
        holder.videoView2.setVideoURI(videoUri)
        holder.videoView2.requestFocus()

        holder.videoView2.setOnPreparedListener {mediaPlayer ->
            //video is prepared to play
            mediaPlayer.start()
        }
        holder.videoView2.setOnInfoListener(MediaPlayer.OnInfoListener{ mp, what, extra->
            //check if buffering/rendering etc
            when(what){
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ->{
                    //rendering started
                    holder.progressBar2.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
                    //buffering started
                    holder.progressBar2.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END ->{
                    //buffering ended
                    holder.progressBar2.visibility = View.GONE
                    return@OnInfoListener true
                }
            }

            false
        })

        holder.videoView2.setOnCompletionListener {mediaPlayer ->
            //restart video when completed | loop video
            mediaPlayer.start()

        }


    }

    override fun getItemCount(): Int {
        return videoArrayList2!!.size //return size/length or the arraylist
    }


    class HolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //init UI Views
        var videoView2: VideoView = itemView.findViewById(R.id.videoView2)
        var titleTv2: TextView = itemView.findViewById(R.id.titleTv2)
        var timeTv2: TextView = itemView.findViewById(R.id.timeTv2)
        var progressBar2: ProgressBar = itemView.findViewById(R.id.progressBar2)
        var username: TextView = itemView.findViewById(R.id.user_name2)
        var userimage: CircleImageView = itemView.findViewById(R.id.user_image2)
        var like_btn2: ImageView = itemView.findViewById(R.id.like_btn2)
        var like_count2: TextView = itemView.findViewById(R.id.like_count2)
        var comment_btn2: ImageView = itemView.findViewById(R.id.comment_btn2)
        var comment_count2: TextView = itemView.findViewById(R.id.comment_count2)


    }


}

