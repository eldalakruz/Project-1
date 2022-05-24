package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.PostAdapter
import com.example.myapplication.Model.Post
import com.example.myapplication.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
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

    private var followingLis: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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
        Log.e("retrievePostsHomeFragment","check 10")
        postsRef.addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(pO: DataSnapshot) {
                Log.e("retrievePostsHomeFragment","check 11")
                postList?.clear()

                Log.e("retrievePostsHomeFragment","check 12")
                for (snapshot in pO.children)
                {
                    Log.e("retrievePostsHomeFragment","check 13")
                    val post = snapshot.getValue(Post::class.java)
                    Log.e("retrievePostsHomeFragment","check 14")
                    for (id in (followingList as ArrayList<String>))
                    {
                        Log.e("retrievePostsHomeFragment","check 15")
                        if (post!!.getPublisher() == id)
                        {
                            Log.e("retrievePostsHomeFragment","check 16")
                            postList!!.add(post)
                            Log.e("retrievePostsHomeFragment","check 17")
                        }
                        Log.e("retrievePostsHomeFragment","check 18")
                        postAdapter!!.notifyDataSetChanged()
                    }

                    Log.e("retrievePostsHomeFragment","check 19")

                }
                Log.e("retrievePostsHomeFragment","check 20")

                for (snapshot in pO.children)
                {
                    Log.e("HomeFragment","check 19")
                    val post = snapshot.getValue(Post::class.java)
                    Log.e("HomeFragment","check 20")

                    postList!!.add(post!!)
                    postAdapter!!.notifyDataSetChanged()
                    Log.e("HomeFragment","check 21")
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("retrievePostsHomeFragment","check 21")
            }
        })
    }

    //..............................................................................................................


    private fun retrievePoststwo() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        Log.e("retrievePostsHomeFragment","check 10")
        postsRef.addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(pO: DataSnapshot) {
                Log.e("HomeFragment","check 11")
                postList?.clear()

                Log.e("HomeFragment","check 12")
                for (snapshot in pO.children)
                {
                    Log.e("HomeFragment","check 13")
                    val post = snapshot.getValue(Post::class.java)
                    Log.e("HomeFragment","check 14")

                    postList!!.add(post!!)
                    postAdapter!!.notifyDataSetChanged()
                    Log.e("HomeFragment","check 19")
                }
                Log.e("HomeFragment","check 20")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment","check 21")
            }
        })
    }
}
