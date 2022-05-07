package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.AccountSettingsActivity
import com.example.myapplication.Adapter.MyImagesAdapter
import com.example.myapplication.Model.Post
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
import kotlin.collections.ArrayList

class SearchUserFragment : Fragment() {

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


    var postList : List<Post>? = null
    var myImagesAdapter : MyImagesAdapter? = null

    var myImagesAdapterSavedImg : MyImagesAdapter? = null
    var postListSaved : List<Post>? = null
    var mySavesImg : List<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        profileId = arguments?.getString("userpublisherId").toString()


        // recycler View for upload  Images
        var recyclerViewUploadImages : RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages.layoutManager = linearLayoutManager


        postList = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapter



        // recycler View for saved Images
        var recyclerViewSavedImages : RecyclerView
        recyclerViewSavedImages = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2 : LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2


        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let { MyImagesAdapter(it, postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter = myImagesAdapterSavedImg


        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE


        var uploadImgesBtn : ImageView
        uploadImgesBtn = view!!.findViewById(R.id.image_grid_view_btn)
        uploadImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
        }


        var savedImgesBtn : ImageView
        savedImgesBtn = view!!.findViewById(R.id.image_grid_save_btn)
        savedImgesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
        }


        if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }

        editaccountsettingsbtn = view.findViewById(R.id.edit_account_settings_btn)
        editaccountsettingsbtn.setOnClickListener {
            val getButtonText = editaccountsettingsbtn.text.toString()

            when
            {
                getButtonText == "Edit Profile"  ->
                    startActivity(Intent(context, AccountSettingsActivity::class.java))

                getButtonText == "Follow"  -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)

                    }
                }

                getButtonText == "Following"  -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }
                }
            }

        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()


        return view
    }

    private fun checkFollowAndFollowingButtonStatus()
    {
        val followingRef =  firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }
        if (followingRef != null)
        {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child(profileId).exists())
                    {
                        editaccountsettingsbtn.text = "Following"
                    }
                    else
                    {
                        editaccountsettingsbtn.text = "Follow"
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
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
                    (postList as ArrayList<Post>).clear()

                    for (snapshot in pO.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(profileId))
                        {
                            (postList as ArrayList<Post>).add(post)
                        }

                        Collections.reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
//                if (context != null)
//                {
//                    return
//                }

                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    proimageprofilefrag = view!!.findViewById(R.id.pro_image_profile_frag)
                    profilefragmentusername = view!!.findViewById(R.id.profile_fragment_username)
                    fullnameprofilefrag = view!!.findViewById(R.id.full_name_profile_frag)
                    bioprofilefrag = view!!.findViewById(R.id.bio_profile_frag)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(proimageprofilefrag)
                    profilefragmentusername?.text = user!!.getUsername()
                    fullnameprofilefrag?.text = user!!.getFullname()
                    bioprofilefrag?.text = user!!.getBio()


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
                    (postListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavesImg!!)
                        {
                            if (post!!.getPostid() == key)
                            {
                                (postListSaved as ArrayList<Post>).add(post!!)
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