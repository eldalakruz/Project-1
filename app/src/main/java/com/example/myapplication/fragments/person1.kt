package com.example.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AdapterVideo
import com.example.myapplication.AddVideoActivity
import com.example.myapplication.Model.ModelVideo
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File



class person1 : Fragment() {

    private lateinit var addVideoFab : FloatingActionButton

    //arraylist for video list
    private lateinit var videoArrayList: ArrayList<ModelVideo>
    //adapter
    private lateinit var adapterVideo: AdapterVideo

    private lateinit var videosRv: RecyclerView

    private var postId = ""
    private var publisherId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_person1, container, false)

        val intent = arguments
        postId = intent?.getString("postId").toString()
        publisherId = intent?.getString("publisherId").toString()

        //function call to load video from firebase
       loadVideosFromFirebase()

        addVideoFab = view.findViewById(R.id.addVideoFab)

            addVideoFab.setOnClickListener {
            val intent = Intent(this.requireContext(), AddVideoActivity::class.java)
            intent.putExtra("publishId", publisherId).toString()
            startActivity(intent)
        }

        return view
    }


    private fun loadVideosFromFirebase() {

       //Read postId in internal storage
        val path = context?.getExternalFilesDir(null)
        val folder = File(path,"temp")
        println(folder.exists())
        val file = File(folder, "postid.txt")
        val connt = file.readText()

        //init arraylist before adding data into it
        videoArrayList = ArrayList()

        //reference of firebase db
        val ref = FirebaseDatabase.getInstance().reference
            .child("CampaignPage")
            .child("CampageVideo1")
            .child(connt)
        Log.e("sample","sam:$ref")


        ref.addValueEventListener(object : ValueEventListener {


           override fun onDataChange(snapshot: DataSnapshot) {

                //clear list before adding data into it
                videoArrayList.clear()
                for (ds in snapshot.children) {
                    //get data as model
                    val modelVideo = ds.getValue(ModelVideo::class.java)
                    //add to array list
                    videoArrayList.add(modelVideo!!)
                }
                //setup adapter
                adapterVideo = AdapterVideo(this@person1.requireContext(), videoArrayList)
                //set adapter to recyclerview
                videosRv = view!!.findViewById(R.id.videosRv) as RecyclerView
                videosRv.adapter = adapterVideo

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}
