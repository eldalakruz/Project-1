package com.example.myapplication.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.AccountSettingsActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var editProfile : Button
    private lateinit var totalfollowers : TextView
    private lateinit var totalfollowing : TextView

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var editaccountsettingsbtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId","none").toString()
        }

        if (profileId == firebaseUser.uid)
        {
            editaccountsettingsbtn = view.findViewById(R.id.edit_account_settings_btn)
            editaccountsettingsbtn.text = "Edit Profile"
        }
        else if (profileId == firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }

        editProfile = view.findViewById(R.id.edit_account_settings_btn)
        editProfile.setOnClickListener {
            startActivity(Intent(context,AccountSettingsActivity::class.java))
        }

        getFollowers()
        getFollowings()

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
            followingRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    {
                        if (snapshot.child(profileId).exists())
                        {
                            editaccountsettingsbtn.text = "Following"
                        }
                        else
                        {
                            editaccountsettingsbtn.text = "Follow"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun getFollowers()
    {
        val followersRef =  firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Followers")

        }
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
        val followersRef =  firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }
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

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
    }

}






