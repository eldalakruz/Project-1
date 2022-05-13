package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class AddVideoActivity : AppCompatActivity() {


   // private lateinit var actionBar: ActionBar
    private lateinit var uploadVideoBtn : Button
    private lateinit var pickVideoFab : FloatingActionButton
    private lateinit var videoView : VideoView
    private lateinit var titleEt : EditText

   // private var firebaseUser : FirebaseUser? = null

    //constants to pick video
    private val VIDEO_PICK_GALLERY_CODE = 101
    private val VIDEO_PICK_CAMERA_CODE = 102

    //constant to request camera permission to record video from camera
    private val CAMERA_REQUEST_CODE = 103

    //array for camera request permissions
    private lateinit var cameraPermissions :Array<String>

    //progress bar
    private lateinit var progressDialog: ProgressDialog

    private var videoUri: Uri? = null //uri of picked video

    private var title : String = ""

    private var postId   = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)


        //init camera permission array
        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //init progressbar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setMessage("Uploading Video...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, upload video
        uploadVideoBtn = findViewById(R.id.uploadVideoBtn)
        uploadVideoBtn.setOnClickListener {
            //get title
            titleEt = findViewById(R.id.titleEt)
            title = titleEt.text.toString().trim()
            if (TextUtils.isEmpty(title)){
                //no title is entered
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            }
            else if (videoUri == null){
                //video is not picked
                Toast.makeText(this, "Pick the video first", Toast.LENGTH_SHORT).show()
            }
            else{
                //title entered,video picked, so now upload video
                uploadVideoFirebase()


            }

        }
        pickVideoFab = findViewById(R.id.pickVideoFab)
        pickVideoFab.setOnClickListener {
            videoPickDialog()
        }


    }

    private fun uploadVideoFirebase() {

        //PostId file read in internal storage
        val path = this.getExternalFilesDir(null)
        val folder = File(path,"temp")
        println(folder.exists())
        val file = File(folder, "postid.txt")
        val connt = file.readText()

        //show progress
        progressDialog.show()

        //timestamp
        val timestamp = ""+System.currentTimeMillis()

        //file path and name in firebase storage
        val filePathAndName = "Video/mani_$timestamp"

        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        //upload video using uri of video to storage
        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                //uploaded,get url of uploaded video
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);

               // ref.removeValue()

                val downloadUri = uriTask.result
                if (uriTask.isSuccessful){
                    //video url is received successfully
                    val dbReference = FirebaseDatabase.getInstance().reference.child("Posts")
                        .child("CampaignPage").child("CampageVideo1").child(connt)
                    //now we can add video details to firebase db
                    val hashMap = HashMap<String, Any>()
                  // val postId = dbReference.push().key

                   // ref.removeValue()

                    hashMap["id"] = "$timestamp"
                    hashMap["title"] = "$title"
                    hashMap["timestamp"] = "$timestamp"
                    hashMap["videoUri"] = "$downloadUri"
                    hashMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    hashMap["postid"] = "$connt"

                    Log.e("sample","test:$connt")

                    //put the above info to db
                    dbReference.child(timestamp)
                        .setValue(hashMap)
                        .addOnSuccessListener { taskSnapshot ->
                            //video info added successfully
                            progressDialog.dismiss()
                            Toast.makeText(this,"Video Uploaded", Toast.LENGTH_SHORT).show()

                          // val intent = Intent(this@AddVideoActivity,person1::class.java)
                           // startActivity(intent)
                          //  finish()

                        }

                        .addOnFailureListener { e ->
                            //failed adding video info
                            progressDialog.dismiss()
                            Toast.makeText(this,"${e.message}", Toast.LENGTH_SHORT).show()

                        }

                }

            }

            .addOnFailureListener { e ->
                //failed uploading
                progressDialog.dismiss()
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()
            }
       // finish()
    }

    private fun setVideoToView() {
        //set the picked video to video view

        //video play controls
        videoView = findViewById(R.id.videoView)
        val mediaController = MediaController(this)

        mediaController.setAnchorView(videoView)

        //set media controller

        videoView.setMediaController(mediaController)
        //set video uri
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            //when video is ready, by default don't play automatically
            videoView.pause()
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
            }
            .show()
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

    private fun videoPickGallery(){
        //video pick intent gallery
        val intent = Intent()
        intent.type ="video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose video"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    private fun videoPickCamera(){
        //video pick intent camera
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    //handle permission results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE ->
                if (grantResults.size > 0){
                    //check if permission allowed or denied
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted){
                        //both permissions allowed
                        videoPickCamera()
                    }
                    else{
                        //both or one of those are denied
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //handle video pick results
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK){
            //video is picked from camera or gallery
            if (requestCode == VIDEO_PICK_CAMERA_CODE){
                //video picked from camera
                videoUri == data!!.data
                setVideoToView()
            }
            else if (requestCode == VIDEO_PICK_GALLERY_CODE){
                //video picked from gallery
                videoUri = data!!.data
                setVideoToView()
            }
        }
        else{
            //cancelled picking video
            Toast.makeText(this,"Canceld",Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)

    }




}




