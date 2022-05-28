package com.example.myapplication

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AnswerAdapter
import com.example.myapplication.Model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class Add_answer : AppCompatActivity() {

    private var id = ""
    private var publisher = ""
    private var imageUri1 = ""
    private var firebaseUser : FirebaseUser? = null

    private lateinit var write_question_answer : TextView
    private lateinit var answerRv : RecyclerView
    private lateinit var profile_image_answer : CircleImageView
    private lateinit var add_answer : EditText
    private lateinit var postBtn_answer : ImageView
    private lateinit var share_link : ImageView
    private lateinit var imageonanswer : ImageView

    private val VIDEO_PICK_GALLERY_CODE = 101
    private val VIDEO_PICK_CAMERA_CODE = 102
    private val CAMERA_REQUEST_CODE = 103

    private lateinit var cameraPermissions :Array<String>

    private lateinit var progressDialog: ProgressDialog

    private var videoUri: Uri? = null

    private var AnswerAdapter : AnswerAdapter? = null
    private var list : ArrayList<ModelAnswer>? = null



    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_answer)

        add_answer = findViewById(R.id.add_answer)

        val intent = intent
        id = intent.getStringExtra("id").toString()
        publisher = intent.getStringExtra("publisher").toString()
        imageUri1 = intent.getStringExtra("imageUri").toString()

        Log.e("sample","test:$imageUri1")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        answerRv = findViewById(R.id.answerRv)

       val linearLayoutManager = LinearLayoutManager(this)
       linearLayoutManager.reverseLayout
       answerRv.layoutManager = linearLayoutManager


            list = ArrayList()
            AnswerAdapter = AnswerAdapter(this, list!!)
            answerRv.adapter = AnswerAdapter




        userInfo()
        getQuestion()
        readAnswer()
        //getimage()

        share_link = findViewById(R.id.share_link)
        share_link.setOnClickListener {
            val path = this.getExternalFilesDir(null)
            val folder = File(path,"image")

            if (folder.exists()){
                val delete =   folder.deleteRecursively()
                Log.e("test","file:$delete")

            }
            folder.mkdirs()
            val file = File(folder, "postid.txt")
            file.appendText("$id")
            videoPickDialog()
        }




        postBtn_answer = findViewById(R.id.postBtn_answer)
        postBtn_answer.setOnClickListener(View.OnClickListener {
            if (add_answer!!.text.toString() == "")
            {
                Toast.makeText(this@Add_answer, "Please write answer first...", Toast.LENGTH_LONG).show()
            }
            else
            {
                addAnswer()
            }
        } )


       // uploadImageFirebase()




    }

    private fun getimage() {

        val usersRef = FirebaseDatabase.getInstance().getReference().child("Question").child(id).child(imageUri1)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<question>(question::class.java)

                    profile_image_answer = findViewById(R.id.profile_image_answer)

                    Picasso.get().load(user!!.imageUri).into(imageonanswer)



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })





    }

    private fun uploadImageFirebase() {
        val timestamp = "" + System.currentTimeMillis()

        val filePathAndName = "Image/mani_$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);

                val downloadUri = uriTask.result

                if (uriTask.isSuccessful) {
                    //video url is received successfully
                    val dbReference = FirebaseDatabase.getInstance().getReference("Answer").child(id)
                        .child("image")
                    //now we can add video details to firebase db
                    val hashMap = HashMap<String, Any>()
                    // val postId = dbReference.push().key

                    // ref.removeValue()

                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["timestamp"] = "$timestamp"
                    hashMap["imageUri"] = "$downloadUri"
                    hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    // hashMap["postid"] = "$connt"

                    // Log.e("sample", "test:$connt")

                    //put the above info to db
                    dbReference.child(timestamp)
                        .setValue(hashMap)
                        .addOnSuccessListener { taskSnapshot ->
                            //video info added successfully
                            progressDialog.dismiss()
                            Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()


                        }

                        .addOnFailureListener { e ->
                            //failed adding video info
                            progressDialog.dismiss()
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()

                        }

                }
            }

    }

    private fun videoPickDialog() {

            val options = arrayOf("Camere", "Photo" , "Video")

            //alart dialog
            val builder = AlertDialog.Builder(this)
            //title
            builder.setTitle("Select")
                .setItems(options) { dialogInterface, i ->
                    if (i == 0) {
                        //camera clicked
                        if (!checkCameraPermissions()) {
                            //permission was not allowed, request
                            requestCameraPermission()
                        } else {
                            //permission was allowed,pick video
                            videoPickCamera()
                        }
                    } else if (i == 1){
                        //gallery clicked
                              //  videoPickGallery()
                        startActivity(Intent(this@Add_answer,ImagePostAnswer::class.java))
                        intent.putExtra("id",id)


                    }

                }
                .show()

    }



    private fun videoPickCamera() {


                    val intent = Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA)

                    startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)

//                    val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(intent1, VIDEO_PICK_CAMERA_CODE)
    }

    private fun videoPickGallery() {

//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        startActivityForResult(
//            Intent.createChooser(intent, RESULT_OK.toString()),
//        VIDEO_PICK_GALLERY_CODE
//        )
//
           AddVideoActivity()

    }


    private fun checkCameraPermissions(): Boolean {
        //check if camera permission i.e. camera and storage is allowed or not
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        //return result as true/false
        return result1 && result2

    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun readAnswer() {

        val answersRef = FirebaseDatabase.getInstance().getReference()
            .child("Answer")
            .child(id)

        answersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot)
            {
                if (pO.exists())
                {
                    list?.clear()

                    for (snapshot in pO.children)
                    {
                        val comment = snapshot.getValue(ModelAnswer::class.java)
                        list?.add(comment!!)
                    }
//                    AnswerAdapter = AnswerAdapter(this@Add_answer,list!!)
//                    answerRv.adapter = AnswerAdapter

                    AnswerAdapter!!.notifyDataSetChanged()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getQuestion() {
        val questionRef = FirebaseDatabase.getInstance()
            .reference.child("Question")
            .child(id).child("Question")

        questionRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val answer = snapshot.value.toString()

                    write_question_answer = findViewById(R.id.write_question_answer)
                    write_question_answer.setText(answer)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    profile_image_answer = findViewById(R.id.profile_image_answer)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.ic_person_black).into(profile_image_answer)



                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun addAnswer() {
        val answerRef = FirebaseDatabase.getInstance().getReference()
            .child("Answer")
            .child(id)

        val answerMap = HashMap<String, Any>()
        answerMap["answer"] = add_answer!!.text.toString()
        answerMap["publisher"] = firebaseUser!!.uid

        answerRef.push().setValue(answerMap)

        add_answer!!.text.clear()

    }
}