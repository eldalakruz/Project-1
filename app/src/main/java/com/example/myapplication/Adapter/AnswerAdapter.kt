package com.example.myapplication.Adapter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.ModelAnswer
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

class AnswerAdapter(
    private var context: Context,
    private var list: ArrayList<ModelAnswer>,
) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    private var firebaseUser : FirebaseUser? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerAdapter.ViewHolder {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val view = LayoutInflater.from(context).inflate(R.layout.sample_answer, parent, false)

        return ViewHolder(view)


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
        val answer = list!![position]

        if (holder.answer.visibility == View.GONE )
        {
            holder.answer.setTransitionVisibility(View.VISIBLE)

        }

       if ( holder.answerImage.visibility == View.GONE) {
           holder.answerImage.setTransitionVisibility(View.VISIBLE)
       }

//        if ( holder.answerVideo.visibility == View.GONE ) {
//            holder.answerVideo.setTransitionVisibility(View.VISIBLE)
//        }

        val id: String? = answer.id
        val title: String? = answer.title
        val timestamp: String? = answer.timestamp
        val imageUri: String? = answer.imageUri

//       val id1: String? = answer.id1
//       val title1: String? = answer.title1
//       val timestamp1: String? = answer.timestamp1
//        val videoUri: String? = answer.videoUri

    /*    if (answer.getimage()?.isEmpty() == true)
        {
            holder.answerImage.setImageResource(R.drawable.profile)
        }
        else {
            Picasso.get().load(answer.getimage()).into(holder.answerImage)
        }    */


        Picasso.get().load(answer.getimage()).into(holder.answerImage)

           holder.answer.text = answer.getanswer()
        Log.e("sample","test:$answer")


        getUserInfo(holder.user_profile_post_answer,holder.user_name_answer,answer.getPublisher())

    }

  /*  private fun setVideoUrl(answer : ModelAnswer, holder: ViewHolder) {


        //get video uri
        val videoUrl : String? = answer.videoUri //"https://firebasestorage.googleapis.com/v0/b/my-application-7d428.appspot.com/o/Video%2Fmani_1652686223778?alt=media&token=df412ee5-e2d2-4701-af40-4db0e98fb830"


        //MediaController for play/pause/time etc
        val mediaController = MediaController(context)
        mediaController.setAnchorView(holder.answerVideo)
        val videoUri = Uri.parse(videoUrl)

        holder.answerVideo.setMediaController(mediaController)
        holder.answerVideo.setVideoURI(videoUri)
        holder.answerVideo.requestFocus()

        holder.answerVideo.setOnPreparedListener {mediaPlayer ->
            //video is prepared to play
            mediaPlayer.start()
        }
        holder.answerVideo.setOnInfoListener(MediaPlayer.OnInfoListener{ mp, what, extra->
            //check if buffering/rendering etc
            when(what){
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ->{
                    //rendering started
                   // holder.progressBar.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
                    //buffering started
                  //  holder.progressBar.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END ->{
                    //buffering ended
                 //   holder.progressBar.visibility = View.GONE
                    return@OnInfoListener true
                }
            }

            false
        })

        holder.answerVideo.setOnCompletionListener {mediaPlayer ->
            //restart video when completed | loop video
            mediaPlayer.start()

        }

    }  */


    private fun setimageUrl(answer: ModelAnswer, holder: ViewHolder) {

        val imageUrl: String? = answer.imageUri

        val mediaController = MediaController(context)

        mediaController.setAnchorView(holder.answerImage)
        val imageUri = Uri.parse(imageUrl)
        holder.answerImage.setImageURI(imageUri)
        holder.answerImage.requestFocus()

    }

    private fun getUserInfo(user_profile_post_answer :CircleImageView, user_name_answer : TextView,publisherID : String)
    {
        val userRef = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(publisherID)

        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {
                if (pO.exists())
                {
                    val user = pO.getValue(User::class.java)


//                   Picasso.get().load(user.getImage()).placeholder(R.drawable.ic_person_black).into(user_profile_post_answer)

//                    if (user!!.getImage().isEmpty())
//                    {
//                        user_profile_post_answer.setImageResource(R.drawable.ic_person_black)
//                    }
//                    else {
//                        Picasso.get().load(user.getImage())
//                            .placeholder(R.drawable.ic_person_black).into(user_profile_post_answer)
//                    }

                    user_name_answer.text = user!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return list!!.size
    }


    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {

         var user_profile_post_answer : CircleImageView
         var user_name_answer : TextView
         var answer : TextView
         lateinit var answerImage : ImageView
    //     lateinit var answerVideo : VideoView
        // lateinit var progressBar : ProgressBar

        init {
            user_profile_post_answer = itemView.findViewById(R.id.user_profile_post_answer)
            user_name_answer = itemView.findViewById(R.id.user_name_answer)
            answer = itemView.findViewById(R.id.answerText)
            answerImage = itemView.findViewById(R.id.answerImage)
            //answerVideo = itemView.findViewById(R.id.answerVideo)
  //          progressBar = itemView.findViewById(R.id.progressBar)
        }

    }


}