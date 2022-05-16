package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.AccountSettingsActivity
import com.example.myapplication.Adapter.PostAdapter
import com.example.myapplication.AddPostActivity
import com.example.myapplication.Model.Post
import com.example.myapplication.PostActivity
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    private var followingLis: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnpost = view.findViewById<FloatingActionButton>(R.id.pickVideoFab_video)
        btnpost.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java))
        }

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        followingLis = ArrayList()

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        //checkFollowings()
        retrievePoststwo()

        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        Log.e("checkFollowingsHomeFragment","check 1")
        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {
                Log.e("checkFollowingsHomeFragment","check 2")
                if (pO.exists())
                {
                    Log.e("checkFollowingsHomeFragment","check 3")
                    (followingList as ArrayList<String>).clear()
                    Log.e("checkFollowingsHomeFragment","check 4")
                    for (snapshot in pO.children)
                    {
                        Log.e("checkFollowingsHomeFragment","check 5")
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    Log.e("checkFollowingsHomeFragment","check 6")
                    retrievePosts()
                    Log.e("checkFollowingsHomeFragment","check 7")
                }
                Log.e("checkFollowingsHomeFragment","check 8")
            }
            override fun onCancelled(pO: DatabaseError) {
                Log.e("checkFollowingsHomeFragment","check 9")
            }
        })
    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(pO: DataSnapshot) {
                postList?.clear()
                for (snapshot in pO.children)
                {
                    val post = snapshot.getValue(Post::class.java)
                    for (id in (followingList as ArrayList<String>))
                    {
                        if (post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }

                }

                for (snapshot in pO.children)
                {
                    val post = snapshot.getValue(Post::class.java)
                    postList!!.add(post!!)
                    postAdapter!!.notifyDataSetChanged()
                }

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //..............................................................................................................


    private fun retrievePoststwo() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                postList?.clear()
                for (snapshot in pO.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    postList!!.add(post!!)
                    postAdapter!!.notifyDataSetChanged()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}

