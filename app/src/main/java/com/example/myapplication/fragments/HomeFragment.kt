package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.Adapter.PostAdapter
import com.example.myapplication.Model.Post
import com.example.myapplication.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private companion object{
        //TAG for debugging
        private const val TAG = "NATIVE_AD_TAG"
    }

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

   // private var followingLis: MutableList<Post>? = null

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

//        val rightarrow = view.findViewById<ImageView>(R.id.Move_to_poll_page)
//        rightarrow.setOnClickListener {
//            startActivity(Intent(context,PollingActivity::class.java))
//        }


        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


    //    followingLis = ArrayList()

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        retrievePoststwo()

        MobileAds.initialize(context){

            Log.d(TAG, "onCreate: onInitCompleted")

            //set your test devices .check your logcat for the hashed device ID to
            // get test ads on a physical device e.g.
            Log.d(TAG, "onCreate: first")
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(listOf("TEST_DEVICE_ID_HERE"," TEST_DEVICE_ID_HERE")).build()
            )
            Log.d(TAG, "onCreate: end")

        }
        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")


        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {

                if (pO.exists())
                {

                    (followingList as ArrayList<String>).clear()

                    for (snapshot in pO.children)
                    {

                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()

                }

            }
            override fun onCancelled(pO: DatabaseError) {

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

