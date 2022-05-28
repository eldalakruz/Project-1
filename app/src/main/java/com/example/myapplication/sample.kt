package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.QuestionAdapter
import com.example.myapplication.Model.User
import com.example.myapplication.Model.question
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class sample : AppCompatActivity() {

    private lateinit var question_create_Fab : FloatingActionButton

    private lateinit var questionArrayList: ArrayList<question>
    private  lateinit var questionadapter : QuestionAdapter

    private lateinit var questionRv : RecyclerView
    private lateinit var user_image : CircleImageView


    private var firebaseUser : FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        questionRv = findViewById(R.id.questionRv)

       // val linearLayoutManager = LinearLayoutManager(context)



        userInfo()
        loadquestionFromFirebase()


        question_create_Fab = findViewById(R.id.question_create_Fab)
        question_create_Fab.setOnClickListener {
            val intent = Intent(this@sample, Add_questiom::class.java)
            startActivity(intent)
        }

    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

//                    user_image = findViewById(R.id.user_image)

       //             Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_person_black).into(user_image)



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
   }

    private fun loadquestionFromFirebase() {

        questionArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Question").orderByKey()

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                questionArrayList.clear()

                for (ds in snapshot.children){
                    val questionmodel = ds.getValue(question::class.java)

                    questionArrayList.add(questionmodel!!)
                }

                questionadapter = QuestionAdapter(this@sample,questionArrayList)

                questionRv = findViewById(R.id.questionRv) as RecyclerView

                questionRv.adapter=questionadapter

               // questionadapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}