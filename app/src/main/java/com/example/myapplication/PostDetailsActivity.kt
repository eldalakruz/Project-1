package com.example.myapplication


import android.content.ContentValues.TAG
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AllPostsAdapter
import com.example.myapplication.Model.AllPost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase


class PostDetailsActivity : AppCompatActivity() {

    private var postAdapter: AllPostsAdapter? = null
    private var postList: MutableList<AllPost>? = null
    private var postId : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)


        var recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager


        postList = ArrayList()
        postAdapter = let { AllPostsAdapter(it, postList as ArrayList<AllPost>) }
        recyclerView.adapter = postAdapter



        linqRetrieveFunction()


        if (postId != "") {
            retrievePosts()
        }else{
            linqRetrieveFunction()
        }



    }

    private fun retrievePosts() {

        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {

                postList?.clear()

                    val post = pO.getValue(AllPost::class.java)

                    postList!!.add(post!!)

                    postAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    fun linqRetrieveFunction() {


        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData : PendingDynamicLinkData? ->

                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                deepLink?.let { uri ->
                    val path = uri.toString().substring(deepLink.toString().lastIndexOf("/") + 1)

                    // In case if you have multiple shareable items such as User Post, User Profile,
                    // you can check if
                    // the uri contains the required string.
                    // In our case we will check if the path contains the string, 'post'

                    when {
                        uri.toString().contains("post") -> {
                            // In my case, the ID is an Integer
                            postId = path

                            retrievePosts()

                            // Call your API or DB to get the post with the ID [postId]
                            // and open the required screen here.
                        }
                    }
                }
            }.addOnFailureListener {
                // This lambda will be triggered when there is a failure.
                // Handle
                Log.i(TAG, "handleIncomingDeepLinks: ${it.message}")
            }


    }


    private fun retrievePoststwo() {

        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot) {
                postList?.clear()
                for (snapshot in pO.children)
                {
                    val post = snapshot.getValue(AllPost::class.java)

                    postList!!.add(post!!)
                    postAdapter!!.notifyDataSetChanged()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}