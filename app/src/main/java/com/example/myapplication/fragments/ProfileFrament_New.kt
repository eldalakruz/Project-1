package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.AccountSettingsActivity
import com.example.myapplication.Adapter.MyImagesAdapter
import com.example.myapplication.Adapter.MyImagesAdapter2
import com.example.myapplication.Model.AllPost
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.User
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*


class ProfileFrament_New : Fragment() {

    private lateinit var totalfollowers : TextView
    private lateinit var totalfollowing : TextView

    private lateinit var proimageprofilefrag : de.hdodenhof.circleimageview.CircleImageView
    private lateinit var profilefragmentusername : TextView
    private lateinit var fullnameprofilefrag : TextView
    private lateinit var bioprofilefrag : TextView

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var editaccountsettingsbtn: Button
    private lateinit var totalposts : TextView

    var pollpostList : List<AllPost>? = null
    var myImagesAdapter : MyImagesAdapter? = null

    var postList : List<Newpost>? = null
    var myImagesAdapter2 : MyImagesAdapter2? = null

    var myImagesAdapterSavedImg : MyImagesAdapter? = null
    var postListSaved : List<AllPost>? = null
    var mySavesImg : List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile_frament__new, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId","none").toString()
        }


        // recycler View for upload  Images
        var recyclerViewUploadImages : RecyclerView = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        pollpostList = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it, pollpostList as ArrayList<AllPost>) }
        recyclerViewUploadImages.adapter = myImagesAdapter

        // recycler View for saved Images
        var recyclerViewSavedImages : RecyclerView = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2 : LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let { MyImagesAdapter(it, postListSaved as ArrayList<AllPost>) }
        recyclerViewSavedImages.adapter = myImagesAdapterSavedImg

        //recycler View from the Newpost
        var recyclerviewPostpage2 : RecyclerView = view.findViewById(R.id.recycler_view_Post_page2)
        recyclerviewPostpage2.setHasFixedSize(true)
        val linearLayoutManager3 : LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerviewPostpage2.layoutManager = linearLayoutManager3

        postList = ArrayList()
        myImagesAdapter2 = context?.let { MyImagesAdapter2(it, postList as ArrayList<Newpost>) }
        recyclerviewPostpage2.adapter = myImagesAdapter2

        recyclerviewPostpage2.visibility = View.GONE
        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE


        var uploadImgesBtn : ImageView
        uploadImgesBtn = view!!.findViewById(R.id.image_grid_view_btn)
        uploadImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
            recyclerviewPostpage2.visibility = View.GONE
        }


        var savedImgesBtn : ImageView
        savedImgesBtn = view!!.findViewById(R.id.image_grid_save_btn)
        savedImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
            recyclerviewPostpage2.visibility = View.GONE
        }

        var postBtn : ImageView = view!!.findViewById(R.id.Post_page2)
        postBtn.setOnClickListener {
            recyclerviewPostpage2.visibility = View.VISIBLE
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.GONE
        }    




        editaccountsettingsbtn = view.findViewById(R.id.edit_account_settings_btn)
        editaccountsettingsbtn.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))


        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()
        post2()

        return view
    }



    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    totalfollowers = view!!.findViewById(R.id.total_followers)
                    totalfollowers.text = snapshot.childrenCount.toString()

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    totalfollowing = view!!.findViewById(R.id.total_following)
                    totalfollowing.text = snapshot.childrenCount.toString()
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
                    (pollpostList as ArrayList<AllPost>).clear()

                    for (snapshot in pO.children)
                    {
                        val post = snapshot.getValue(AllPost::class.java)!!
                        if (post.getPublisher().equals(profileId))
                        {
                            (pollpostList as ArrayList<AllPost>).add(post)
                        }

                        Collections.reverse(pollpostList)
                        myImagesAdapter!!.notifyDataSetChanged()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun post2() {

        val postsRef = FirebaseDatabase.getInstance().reference.child("Post_page_two")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(pO: DataSnapshot)
            {
                if (pO.exists())
                {
                    (postList as ArrayList<Newpost>).clear()

                    for (snapshot in pO.children)
                    {
                        val post = snapshot.getValue(Newpost::class.java)!!
                        if (post.getPublisher() == profileId)
                        {
                            (postList as ArrayList<Newpost>).add(post)
                        }

                        Collections.reverse(postList)
                        myImagesAdapter2!!.notifyDataSetChanged()

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo() {

        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {

                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    proimageprofilefrag = view!!.findViewById(R.id.pro_image_profile_frag)
                    profilefragmentusername = view!!.findViewById(R.id.profile_fragment_username)
                    fullnameprofilefrag = view!!.findViewById(R.id.full_name_profile_frag)
                    bioprofilefrag = view!!.findViewById(R.id.bio_profile_frag)


                    if (user!!.getImage().isEmpty()) {
                        proimageprofilefrag.setImageResource(R.drawable.profile)
                    } else {
                        Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(proimageprofilefrag)
                    }

                    profilefragmentusername?.text = user!!.getUsername()
                    fullnameprofilefrag?.text = user!!.getFullname()
                    bioprofilefrag?.text = user!!.getBio()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
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
                        val post = snapShot.getValue(AllPost::class.java)!!
                        if (post.getPublisher() == profileId)
                        {
                            postCounter++
                        }
                    }
                    totalposts = view!!.findViewById(R.id.total_posts)
                    totalposts.text = " " + postCounter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun mySaves()
    {
        mySavesImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance().reference.child("Saves")
            .child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (snapshot in dataSnapshot.children)
                    {
                        (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun readSavedImagesData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object  : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    (postListSaved as ArrayList<AllPost>).clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(AllPost::class.java)

                        for (key in mySavesImg!!)
                        {
                            if (post!!.getPostid() == key)
                            {
                                (postListSaved as ArrayList<AllPost>).add(post!!)
                            }
                        }
                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}


