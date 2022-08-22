package com.example.myapplication.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.Adapter.NewpostAdapter
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.Post
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewpostFragment : Fragment() {

    private val rotateOPen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toButton: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }
    private var clicked = false
    private lateinit var btnpost : FloatingActionButton
    private lateinit var btnaddposts : FloatingActionButton
    private lateinit var btnvideo : FloatingActionButton

    private companion object{
        //TAG for debugging
        private const val TAG = "NATIVE_AD_TAG"
    }
    //UI view
         var newpostAdapter : NewpostAdapter? = null
         var postList: MutableList<Newpost>? = null
         var followingList: MutableList<Newpost>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        btnpost = view.findViewById(R.id.pick_or_image_or_video)
        btnpost.setOnClickListener {
            onAddButtonClicked()

        }

        btnaddposts = view.findViewById(R.id.pag_two_imagebtn)
        btnaddposts.setOnClickListener {
            startActivity(Intent(context, Post_Page_Two::class.java))
        }


        btnvideo = view.findViewById(R.id.pag_two_videobtn)
        btnvideo.setOnClickListener {
            startActivity(Intent(context, Post_Page_Two_Video::class.java))
        }


        var recyclerView: RecyclerView? = view.findViewById(R.id.recycler_view_post_post)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        if (recyclerView != null) {
            recyclerView.layoutManager = linearLayoutManager
        }
        //initialize mobiles ads sdk (e.g.if you want to ad test device ids to get test ads)

        postList = ArrayList()
        newpostAdapter = context?.let { NewpostAdapter(it, postList as ArrayList<Newpost>) }
        if (recyclerView != null) {
            recyclerView.adapter = newpostAdapter
        }

        retrievePoststwo()

        MobileAds.initialize(context){

            //set your test devices .check your logcat for the hashed device ID to
            // get test ads on a physical device e.g.
            Log.d(TAG, "onCreate: first")
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("TEST_DEVICE_ID_HERE"," TEST_DEVICE_ID_HERE")).build()
            )
            Log.d(TAG, "onCreate: end")
        }

     return  view
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked)
        {
            btnaddposts.visibility = View.VISIBLE
            btnvideo.visibility = View.VISIBLE
        }
        else
        {
            btnaddposts.visibility = View.INVISIBLE
            btnvideo.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked)
        {
            btnvideo.startAnimation(fromBottom)
            btnaddposts.startAnimation(fromBottom)
            btnpost.startAnimation(rotateOPen)
        }
        else
        {
            btnvideo.startAnimation(toButton)
            btnaddposts.startAnimation(toButton)
            btnpost.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            btnaddposts.isClickable = true
            btnaddposts.isClickable = true
        } else {
            btnaddposts.isClickable = false
            btnaddposts.isClickable = false
        }
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in snapshot.children)
                    {
                        snapshot.key?.let {

                            (followingList as ArrayList<String>).add(it)
                        }
                    }

                    retrieveNewpost()

                }


            }

            override fun onCancelled(snapshot: DatabaseError) {

            }
        })

    }

    private fun retrieveNewpost() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("NewPost")
        postsRef.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot) {
               postList?.clear()
                for (snapshot in pO.children)
                {
                    val Newpost = snapshot.getValue(Newpost::class.java)
                    for (id in (followingList as ArrayList<String>))
                    {
                        if (Newpost!!.getPublisher() == id)

                        {

                        postList!!.add(Newpost)
                        }

                        newpostAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrievePoststwo() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Post_page_two")
        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                postList?.clear()
                for (snapshot in pO.children)
                {
                    val Newpost = snapshot.getValue(Newpost::class.java)

                    postList!!.add(Newpost!!)
                    newpostAdapter!!.notifyDataSetChanged()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}




