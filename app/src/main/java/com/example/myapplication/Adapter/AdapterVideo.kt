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
import com.example.myapplication.CompaignComments1
import com.example.myapplication.Model.ModelVideo
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


class AdapterVideo(
    private var context: Context,
    private var videoArrayList: ArrayList<ModelVideo>,

) : RecyclerView.Adapter<AdapterVideo.HolderVideo>(){

    private var firebaseUser : FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderVideo {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        //inflate layout row_video.xml
        val view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false)

        return HolderVideo(view)
    }

    override fun onBindViewHolder(holder: HolderVideo, position: Int) {
        /*-----get data, set data, handle clicks etc-----*/

        //get data
        val modelVideo = videoArrayList!![position]

        //get speific data
        val id: String? = modelVideo.id
        val title: String? = modelVideo.title
        val timestamp: String? = modelVideo.timestamp
        val videoUri: String? = modelVideo.videoUri

        //format date e.g. 16/03/2022 11:38am
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp!!.toLong()
        val formattedDateTime = android.text.format.DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString()

        //set data
        holder.titleTv.text = title
        holder.timeTv.text = formattedDateTime
        setVideoUrl(modelVideo, holder)

        getUserInfo(holder.userimage, holder.username, modelVideo.getPublisher())
        isLikes(modelVideo.id!!, holder.like_btn)
        numberOfLikes(holder.like_count, modelVideo.id!!)
        getTotalComments(holder.comment_count, modelVideo.id)

        //like function
        holder.like_btn.setOnClickListener {
            if (holder.like_btn.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(modelVideo.id!!)
                    .child(firebaseUser!!.uid)
                    .setValue(true)

            }
            else
            {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(modelVideo.id!!)
                    .child(firebaseUser!!.uid)
                    .removeValue()

            }
        }

        holder.comment_btn.setOnClickListener {
            val intent = Intent(context, CompaignComments1::class.java)
            intent.putExtra("id" ,modelVideo.id)
            intent.putExtra("publisher" , modelVideo.getPublisher())
            context.startActivity(intent)
        }

    }

    private fun numberOfLikes(likeCount: TextView, id: String)
    {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(id)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                   likeCount.text = snapshot.childrenCount.toString() + "likes"
                }
                else
                {
                    likeCount.text = snapshot.childrenCount.toString() + "likes"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun getTotalComments(commentBtn: TextView, id: String?)
    {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("CompaignComments1").child(id!!)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    commentBtn.text ="view all " + snapshot.childrenCount.toString() + " comments"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun isLikes(postid: String, likeBtn: ImageView)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes")
            .child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists())
                {
                    likeBtn.setImageResource(R.drawable.heart_clicked)
                    likeBtn.tag = "Liked"
                }
                else
                {
                    likeBtn.setImageResource(R.drawable.heart_not_click)
                    likeBtn.tag = "Like"
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

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_person_black).into(userimage)

                    username.text = user!!.getUsername()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setVideoUrl(modelVideo: ModelVideo, holder: HolderVideo) {
        //show progress
        holder.progressBar.visibility = View.VISIBLE

        //get video uri
        val videoUrl: String? = modelVideo.videoUri

        //MediaController for play/pause/time etc
        val mediaController = MediaController(context)
        mediaController.setAnchorView(holder.videoView)
        val videoUri = Uri.parse(videoUrl)

        holder.videoView.setMediaController(mediaController)
        holder.videoView.setVideoURI(videoUri)
        holder.videoView.requestFocus()

        holder.videoView.setOnPreparedListener {mediaPlayer ->
            //video is prepared to play
            mediaPlayer.start()
        }
        holder.videoView.setOnInfoListener(MediaPlayer.OnInfoListener{ mp, what, extra->
            //check if buffering/rendering etc
            when(what){
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ->{
                    //rendering started
                    holder.progressBar.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
                    //buffering started
                    holder.progressBar.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END ->{
                    //buffering ended
                    holder.progressBar.visibility = View.GONE
                    return@OnInfoListener true
                }
            }

            false
        })

        holder.videoView.setOnCompletionListener {mediaPlayer ->
            //restart video when completed | loop video
            mediaPlayer.start()

        }


    }

    override fun getItemCount(): Int {
        return videoArrayList!!.size //return size/length or the arraylist

    }

    //view holder class holds and inits UI Views or row_video.xml
    class HolderVideo(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //init UI Views
        var videoView: VideoView = itemView.findViewById(R.id.videoView)
        var titleTv:TextView = itemView.findViewById(R.id.titleTv)
        var timeTv:TextView = itemView.findViewById(R.id.timeTv)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        var username: TextView = itemView.findViewById(R.id.user_name)
        var userimage: CircleImageView = itemView.findViewById(R.id.user_image)
        var like_btn: ImageView = itemView.findViewById(R.id.like_btn)
        var like_count: TextView = itemView.findViewById(R.id.like_count)
        var comment_btn: ImageView = itemView.findViewById(R.id.comment_btn)
        var comment_count: TextView = itemView.findViewById(R.id.comment_count)

    }

}
