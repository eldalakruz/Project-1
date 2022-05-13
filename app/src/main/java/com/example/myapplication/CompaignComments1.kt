package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.CommentAdapter
import com.example.myapplication.Model.Comment
import com.example.myapplication.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CompaignComments1 : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser : FirebaseUser? = null
    private lateinit var profileimagecomment : de.hdodenhof.circleimageview.CircleImageView
    private lateinit var addcomment : EditText
    private lateinit var postcomment : TextView

    private var commentAdapter : CommentAdapter? = null
    private var commentList : MutableList<Comment>? = null

    private lateinit var postimagecomment : VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compaign_comments1)

        addcomment = findViewById(R.id.add_comment)


        val intent = intent
        postId = intent.getStringExtra("id").toString()
        publisherId = intent.getStringExtra("publisherId").toString()


        firebaseUser = FirebaseAuth.getInstance().currentUser


        var recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this,commentList)
        recyclerView.adapter = commentAdapter


        userInfo()
        readComments()
        getPostImage()



        postcomment = findViewById(R.id.post_comment)
        postcomment.setOnClickListener(View.OnClickListener {
            if (addcomment!!.text.toString() == "")
            {
                Toast.makeText(this@CompaignComments1, "Please write comment first...", Toast.LENGTH_LONG).show()
            }
            else
            {
                addComment()
            }
        })
    }


    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().getReference()
            .child("CompaignComments1")
            .child(postId!!)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = addcomment!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addcomment!!.text.clear()

    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    profileimagecomment = findViewById(R.id.profile_image_comment)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileimagecomment)



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getPostImage()
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId!!).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val image = snapshot.value.toString()

                  //  postimagecomment = findViewById<ImageView>(R.id.post_image_comment)
                  //  Picasso.get().load(image).placeholder(R.drawable.profile).into(postimagecomment)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun readComments()
    {
        val commentRef = FirebaseDatabase.getInstance()
            .reference.child("CompaignComments1")
            .child(postId)

        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot)
            {
                if (pO.exists())
                {
                    commentList!!.clear()

                    for (snapshot in pO.children)
                    {
                        val comment = snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

}