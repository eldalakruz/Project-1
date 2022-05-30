package com.example.myapplication.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.NewpostAdapter
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.Post
import com.example.myapplication.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewpostFragment : Fragment() {

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
        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_post_post)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        //initialize mobiles ads sdk (e.g.if you want to ad test device ids to get test ads)

        postList = ArrayList()
        newpostAdapter = context?.let { NewpostAdapter(it, postList as ArrayList<Newpost>) }
        recyclerView.adapter = newpostAdapter

//        checkFollowings()
        retrievePoststwo()

        MobileAds.initialize(context){

            Log.d(TAG, "onCreate: onInitCompleted")

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
        val postsRef = FirebaseDatabase.getInstance().reference.child("NewPost")
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




