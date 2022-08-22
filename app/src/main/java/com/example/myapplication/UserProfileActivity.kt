package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.MyImagesAdapter
import com.example.myapplication.Adapter.MyImagesAdapter2
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.Post
import com.example.myapplication.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class UserProfileActivity : AppCompatActivity() {

    private var publisherId = ""

    private lateinit var userprofiletotalfollowers : TextView
    private lateinit var userprofiletotalfollowing : TextView

    private lateinit var userprofileproimageprofileactivity : de.hdodenhof.circleimageview.CircleImageView
    private lateinit var userprofileactivityusername : TextView
    private lateinit var userprofilefullnameactivity : TextView
    private lateinit var userprofilebioprofileactivity : TextView

    private lateinit var userprofileprofileId : String
    private lateinit var firebaseUser : FirebaseUser
    private lateinit var userprofileeditaccountsettingsbtn : Button
    private lateinit var userprofiletotalposts : TextView

    var userprofilepostList : List<Newpost>? = null
    var userprofilemyImagesAdapter : MyImagesAdapter2? = null

    var userprofilemyImagesAdapterSavedImg : MyImagesAdapter2? = null
    var userprofilepostListSaved : List<Newpost>? = null
    var userprofilemySavesImg : List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        supportActionBar?.hide()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val intent = intent
        publisherId = intent.getStringExtra("publisherId").toString()


        var recyclerViewUploadImages : RecyclerView
        recyclerViewUploadImages = findViewById(R.id.user_profile_recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(this,3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager


        userprofilepostList = ArrayList()
        userprofilemyImagesAdapter = MyImagesAdapter2(this, userprofilepostList as ArrayList<Newpost>)
        recyclerViewUploadImages.adapter = userprofilemyImagesAdapter


        // recycler View for saved Images
        var recyclerViewSavedImages : RecyclerView
        recyclerViewSavedImages = findViewById(R.id.user_profile_recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2 : LinearLayoutManager = GridLayoutManager(this, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2


        userprofilepostListSaved = ArrayList()
        userprofilemyImagesAdapterSavedImg = MyImagesAdapter2(this, userprofilepostListSaved as ArrayList<Newpost>)
        recyclerViewSavedImages.adapter = userprofilemyImagesAdapterSavedImg


        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE


        var uploadImgesBtn : ImageView
        uploadImgesBtn = findViewById(R.id.user_profile_image_grid_view_btn)
        uploadImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
        }


        var savedImgesBtn : ImageView
        savedImgesBtn = findViewById(R.id.user_profile_image_grid_save_btn)
        savedImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
        }

        if (publisherId != firebaseUser.uid)
        {
            UserProfileCheckFollowAndFollowingButtonStatus()
        }

        userprofileeditaccountsettingsbtn = findViewById(R.id.user_account_settings_btn)
        userprofileeditaccountsettingsbtn.setOnClickListener {
            val getButtonText = userprofileeditaccountsettingsbtn.text.toString()

            when
            {
                getButtonText == "Follow"  -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(publisherId)
                            .setValue(true)
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(publisherId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                }

                getButtonText == "Following"  -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(publisherId)
                            .removeValue()
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(publisherId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }
            }

        }

        userInfo()
        getFollowers()
        getFollowings()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()

    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    userprofileproimageprofileactivity = findViewById(R.id.image_user_profile_activity)
                    userprofileactivityusername = findViewById(R.id.user_profile_activity_username)
                    userprofilefullnameactivity = findViewById(R.id.user_profile_full_name_activity)
                    userprofilebioprofileactivity = findViewById(R.id.user_profile_bio_activity)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(userprofileproimageprofileactivity)
                    userprofileactivityusername.setText(user!!.getUsername())
                    userprofilefullnameactivity.text = user!!.getFullname()
                    userprofilebioprofileactivity.text = user!!.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(publisherId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    userprofiletotalfollowers = findViewById(R.id.user_profile_total_followers)
                    userprofiletotalfollowers.text = snapshot.childrenCount.toString()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(publisherId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    userprofiletotalfollowing = findViewById(R.id.user_profile_total_following)
                    userprofiletotalfollowing.text = snapshot.childrenCount.toString()
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun myPhotos()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {
                if (pO.exists())
                {
                    (userprofilepostList as ArrayList<Post>).clear()

                    for (snapshot in pO.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(publisherId))
                        {
                            (userprofilepostList as ArrayList<Post>).add(post)
                        }

                        Collections.reverse(userprofilepostList)
                        userprofilemyImagesAdapter!!.notifyDataSetChanged()

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getTotalNumberOfPosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    var postCounter = 0

                    for (snapShot in dataSnapshot.children)
                    {
                        val post = snapShot.getValue(Post::class.java)!!
                        if (post.getPublisher() == publisherId)
                        {
                            postCounter++
                        }
                    }
                    userprofiletotalposts = findViewById(R.id.user_profile_total_posts)
                    userprofiletotalposts.text = " " + postCounter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun mySaves() {

        userprofilemySavesImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(publisherId)

        savedRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (snapshot in dataSnapshot.children)
                    {
                        (userprofilemySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun readSavedImagesData() {
        
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object  : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    (userprofilepostListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)

                        for (key in userprofilemySavesImg!!)
                        {
                            if (post!!.getPostid() == key)
                            {
                                (userprofilepostListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }
                    userprofilemyImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    
    private fun UserProfileCheckFollowAndFollowingButtonStatus() {

        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1)
                .child("Following") }

        if (followingRef != null) {

            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child(publisherId).exists())
                    {
                        userprofileeditaccountsettingsbtn.text = "Following"
                    }
                    else
                    {
                        userprofileeditaccountsettingsbtn.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}