package com.example.myapplication.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.QuestionAdapter
import com.example.myapplication.AddPostActivity
import com.example.myapplication.Add_questiom
import com.example.myapplication.Model.question
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView


class AddFragment : Fragment() {

    private lateinit var question_create_Fab : FloatingActionButton
    private lateinit var questionArrayList : ArrayList<question>
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var questionRv : RecyclerView
    private lateinit var user_image : CircleImageView
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add, container, false)

        firebaseUser = FirebaseAuth. getInstance().currentUser

        questionRv = view.findViewById(R.id.questionRv)


        loadquestionFromFirebase()

        question_create_Fab = view.findViewById(R.id.question_create_Fab)
        question_create_Fab.setOnClickListener {
            startActivity(Intent(this.requireContext(), Add_questiom::class.java))
        }

        return view
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

                questionAdapter = QuestionAdapter(this@AddFragment.requireContext(),questionArrayList)
                questionRv = view?.findViewById(R.id.questionRv) as RecyclerView

                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.reverseLayout = true
                linearLayoutManager.stackFromEnd = true
                questionRv.layoutManager = linearLayoutManager

                questionRv.adapter = questionAdapter


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}
