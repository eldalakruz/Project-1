package com.example.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Add_answer
import com.example.myapplication.Model.ModelAnswer
import com.example.myapplication.Model.User
import com.example.myapplication.Model.question
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class QuestionAdapter(
    private var context: Context,
    private var questionArrayList: ArrayList<question>,
) : RecyclerView.Adapter<QuestionAdapter.Holderquestion>() {


    private var firebaseUser : FirebaseUser? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holderquestion {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val view = LayoutInflater.from(context).inflate(R.layout.sample_question, parent, false)

        return Holderquestion(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: Holderquestion, position: Int) {

        val questionlist = questionArrayList!![position]

        val id : String? = questionlist.id
        val question : String? = questionlist.Question
        val timestamp : String? = questionlist.timestamp
        val imageUri : String? = questionlist.imageUri

        Picasso.get().load(questionlist.imageUri).into(holder.imageretrivequestion)

       // val calendar = Calendar.getInstance()
//        calendar.timeInMillis = timestamp!!.toLong()
      //  val formattedDateTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString()



       // holder.timeTv_question.text = formattedDateTime

        holder.show_question.text = question


       getUserInfo(holder.user_image,holder.user_name)
        isLike(questionlist.id!!, holder.thumps_btn)
        isDisLike(questionlist.id!!, holder.thumbsdown_Btn)
        numbersOfLikes(holder.like_count, questionlist.id)
        numberOfDisLikes(holder.dislike_count, questionlist.id)
        getTotalAnswer(holder.answer_count, questionlist.id)



        holder.answer_count.setOnClickListener {
            val intent = Intent(context, Add_answer::class.java)
            intent.putExtra("id", questionlist.id)
            intent.putExtra("publisher", questionlist.getPublisher())
            context.startActivity(intent)
        }


        holder.show_question.setOnClickListener {
            val intent = Intent(context, Add_answer::class.java)
            intent.putExtra("id", questionlist.id)
            Log.e("sample","test:$id")
            intent.putExtra("publisher", questionlist.getPublisher())
            intent.putExtra("imageUri", questionlist.imageUri)
            context.startActivity(intent)
        }

        holder.imageretrivequestion.setOnClickListener {
            val intent = Intent(context, Add_answer::class.java)
            intent.putExtra("id", questionlist.id)
            intent.putExtra("publisher", questionlist.getPublisher())
            context.startActivity(intent)
        }

        holder.thumps_btn.setOnClickListener {
            if (holder.thumps_btn.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("AnswerLikes")
                    .child(questionlist.id!!)
                    .child(firebaseUser!!.uid)
                    .setValue(true)

            }

                FirebaseDatabase.getInstance().reference
                    .child("AnswerDisLikes")
                    .child(questionlist.id!!)
                    .child(firebaseUser!!.uid)
                    .removeValue()


        }

        holder.thumbsdown_Btn.setOnClickListener {
            if (holder.thumbsdown_Btn.tag == "Like")
            {
                FirebaseDatabase.getInstance().reference
                    .child("AnswerDisLikes")
                    .child(questionlist.id!!)
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            }


                FirebaseDatabase.getInstance().reference
                    .child("AnswerLikes")
                    .child(questionlist.id!!)
                    .child(firebaseUser!!.uid)
                    .removeValue()


        }

        holder.commentAnswer_btn.setOnClickListener {
            val intent = Intent(context, Add_answer::class.java)
            intent.putExtra("id", questionlist.id)
            Log.e("sample","test:$id")
            intent.putExtra("publisher", questionlist.getPublisher())
            context.startActivity(intent)

        }

    }

    private fun numberOfDisLikes(dislikeCount: TextView, id: String) {

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("AnswerDisLikes").child(id)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    dislikeCount.text = snapshot.childrenCount.toString()
                }
                dislikeCount.text = snapshot.childrenCount.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun isDisLike(id: String, thumbsdownBtn: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val Ref = FirebaseDatabase.getInstance().reference
            .child("AnswerDisLikes")
            .child(id)

        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists())
                {
                    thumbsdownBtn.setImageResource(R.drawable.ic_thumbs_down_color)
                    thumbsdownBtn.tag = "Liked"
                }
                else
                {
                    thumbsdownBtn.setImageResource(R.drawable.ic_thumbs_down)
                    thumbsdownBtn.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getTotalAnswer(answerCount: TextView, id: String) {

        val answerRef = FirebaseDatabase.getInstance().reference
            .child("Answer").child(id!!)

        answerRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    answerCount.text ="view all " + snapshot.childrenCount.toString() + " contributor"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun numbersOfLikes(likeCount: TextView, id: String) {

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("AnswerLikes").child(id)

        LikesRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    likeCount.text = snapshot.childrenCount.toString()
                }
                    likeCount.text = snapshot.childrenCount.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun isLike(id: String, thumpsBtn: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val Ref = FirebaseDatabase.getInstance().reference
            .child("AnswerLikes")
            .child(id)

        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists())
                {
                    thumpsBtn.setImageResource(R.drawable.ic_thumbs_up_color)
                    thumpsBtn.tag = "Liked"
                }
                else
                {
                    thumpsBtn.setImageResource(R.drawable.ic_thumbs_up)
                    thumpsBtn.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getUserInfo(user_image: CircleImageView, user_name: TextView) {

        val userRef = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(firebaseUser!!.uid)

        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {
                if (pO.exists())
                {
                    val user = pO.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_person_black).into(user_image)

                    user_name.text = user!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    override fun getItemCount(): Int {
       return questionArrayList!!.size
    }


    class Holderquestion(itemView : View) : RecyclerView.ViewHolder(itemView){

        var show_question : TextView = itemView.findViewById(R.id.show_question)
        var user_image : CircleImageView = itemView.findViewById(R.id.user_image)
        var user_name : TextView = itemView.findViewById(R.id.user_name)
       // var timeTv_question : TextView = itemView.findViewById(R.id.timeTv_question)
        var thumps_btn : ImageView = itemView.findViewById(R.id.thumps_btn)
        var commentAnswer_btn : ImageView = itemView.findViewById(R.id.commentAnswer_btn)
        var like_count : TextView = itemView.findViewById(R.id.like_count)
        var answer_count : TextView = itemView.findViewById(R.id.answer_count)
        var imageretrivequestion : ImageView = itemView.findViewById(R.id.imageretrivequestion)
        var thumbsdown_Btn : ImageView = itemView.findViewById(R.id.thumbsdown_Btn)
        var dislike_count : TextView = itemView.findViewById(R.id.dislike_count)

    }


}