package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.NewpostAdapter
import com.example.myapplication.Adapter.PostAdapter
import com.example.myapplication.Model.AllPost
import com.example.myapplication.Model.Newpost
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewPostDetailsFragment : Fragment() {

    private var postAdapter: NewpostAdapter? = null
    private var postList: MutableList<Newpost>? = null
    private var postId : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_new_post_details, container, false)

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences!= null)
        {
            postId = preferences.getString("postId", "none").toString()
        }

        var recyclerView : RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { NewpostAdapter(it, postList as ArrayList<Newpost>) }
        recyclerView.adapter = postAdapter

        retrievePosts()

        return view

    }

    private fun retrievePosts() {

        val postsRef = FirebaseDatabase.getInstance().reference.child("Post_page_two").child(postId)

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                postList?.clear()

                val post = pO.getValue(Newpost::class.java)

                postList!!.add(post!!)

                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}