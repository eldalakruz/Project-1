package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.postsVideoAdapter
import com.example.myapplication.AddPostActivity
import com.example.myapplication.AddPostVideoActivity
import com.example.myapplication.Model.AllPost
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

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

    private var postAdapter: postsVideoAdapter? = null
    private var postList2: ArrayList<AllPost>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        btnpost = view.findViewById(R.id.pickVideoFab_video)
        btnpost.setOnClickListener {
            onAddButtonClicked()

        }

        btnaddposts = view.findViewById(R.id.imagebtn)
        btnaddposts.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java))
        }


        btnvideo = view.findViewById(R.id.videobtn)
        btnvideo.setOnClickListener {
            startActivity(Intent(context, AddPostVideoActivity::class.java))
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

//        postList = ArrayList()
        postList2 = ArrayList()


//        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
//        recyclerView.adapter = postAdapter

        postAdapter = context?.let { postsVideoAdapter(it, postList2 as ArrayList<AllPost>) }
        recyclerView.adapter = postAdapter

        retrievePoststwo()


        return view
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
        if (!clicked)
        {
            btnaddposts.isClickable = true
            btnaddposts.isClickable = true
        }
        else
        {
            btnaddposts.isClickable = false
            btnaddposts.isClickable = false
        }
    }



/*    private fun checkFollowings() {
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
    }                   */


    private fun retrievePoststwo() {

        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                postList2?.clear()
                for (snapshot in pO.children)
                {
                   var post = snapshot.getValue(AllPost::class.java)

                    postList2!!.add(post!!)

                    postAdapter!!.notifyDataSetChanged()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}



