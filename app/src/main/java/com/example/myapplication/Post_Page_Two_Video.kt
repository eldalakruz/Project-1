package com.example.myapplication

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class Post_Page_Two_Video : AppCompatActivity() {

    private lateinit var picVideoBtn : Button
    private lateinit var videoView2 : VideoView
    private lateinit var savenewpostbtn: ImageView
    private var videoUri2: Uri? = null
    private var storagePostPicRef: StorageReference? = null
    private var myUrl = ""

    private lateinit var descriptionpost: EditText

    private val VIDEO_PICK_GALLERY_CODE = 101
    private val VIDEO_PICK_CAMERA_CODE = 102
    private val CAMERA_REQUEST_CODE = 103
    private lateinit var cameraPermissions :Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_page_two_video)

        descriptionpost = findViewById(R.id.description_Video_post)


        storagePostPicRef = FirebaseStorage.getInstance().reference.child("post page two Video")
        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        picVideoBtn = findViewById(R.id.picVideoBtn)
        picVideoBtn.setOnClickListener{
            videoPickDialog()
        }

        savenewpostbtn = findViewById(R.id.save_new_Video_post_btn)
        savenewpostbtn.setOnClickListener { uploadVideo() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK){
            //video is picked from camera or gallery
            if (requestCode == VIDEO_PICK_CAMERA_CODE){
                //video picked from camera
                videoUri2 == data!!.data
                setVideoToView()
            }
            else if (requestCode == VIDEO_PICK_GALLERY_CODE){
                //video picked from gallery
                videoUri2 = data!!.data
                setVideoToView()
            }
        }
        else{
            //cancelled picking video
            Toast.makeText(this,"Canceld", Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)

    }



    private fun uploadVideo() {

        when {
            videoUri2 == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(descriptionpost.text.toString()) -> Toast.makeText(this, "please write full name first", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding New Post")
                progressDialog.setMessage("Please wait, we are adding your picture post...")
                progressDialog.show()

                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".mp4")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(videoUri2!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Post_page_two")
                            val postId = ref.push().key

                            val postMap = HashMap<String, Any>()
                            postMap["postid"] = postId!!
                            postMap["description"] = descriptionpost.text.toString().toLowerCase()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["videoUri"] = myUrl

                            ref.child(postId).updateChildren(postMap)

                            Toast.makeText(this, "Post uploaded successfully.", Toast.LENGTH_LONG).show()

                            val intent = Intent(this@Post_Page_Two_Video, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()

                        } else {
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun videoPickDialog() {
        //option to display in dialog
        val options = arrayOf("Camere", "Gallery")
        //alart dialog
        val builder = AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Video From")
            .setItems(options){ dialogInterface, i->
                if (i==0){
                    //camera clicked
                    if (!checkCameraPermissions()){
                        //permission was not allowed, request
                        requestCameraPermission()
                    }
                    else{
                        //permission was allowed,pick video
                        videoPickCamera()
                    }
                }
                else{
                    //gallery clicked
                    videoPickGallery()
                }
            }.show()
    }

    private fun videoPickCamera(){
        //video pick intent camera
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    private fun videoPickGallery(){
        //video pick intent gallery
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose video"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    private fun requestCameraPermission(){
        //request camera permissions
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions():Boolean{
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

    private fun setVideoToView() {
        //set the picked video to video view

        //video play controls
        videoView2 = findViewById(R.id.Video_post)
        val mediaController = MediaController(this)

        mediaController.setAnchorView(videoView2)

        //set media controller
        videoView2.setMediaController(mediaController)
        //set video uri
        videoView2.setVideoURI(videoUri2)
        videoView2.requestFocus()
        videoView2.setOnPreparedListener {
            //when video is ready, by default don't play automatically
            videoView2.pause()
        }

    }

}