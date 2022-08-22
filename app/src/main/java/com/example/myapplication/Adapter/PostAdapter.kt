package com.example.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.Model.AllPost
import com.example.myapplication.Model.User
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileOutputStream


class PostAdapter(private val mContext: Context,
                  private val mPost: List<AllPost>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    @SuppressLint("ClickableViewAccessibility")

//    lateinit var tvPercent1 : TextView
//    lateinit var tvPercent2 : TextView
//    var flag1 = true
//    var flag2 = true
//    var count1 = 0
//    var count2 = 0


    inner class  ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {

        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var sharebtn: ImageView

        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView
        var campaign_btn: TextView

        var pollquestion : TextView
        var contestantone : TextView
        var contestanttwo : TextView
        var btncontestantone : RadioButton
        var btncontestanttwo : RadioButton

        var seekBar1 : SeekBar
        var seekBar2 : SeekBar

        var tvPercent1 : TextView
        var tvPercent2 : TextView

        val radioGroup : RadioGroup


        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage =  itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            sharebtn = itemView.findViewById(R.id.post_image_share_btn)

            userName = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
            campaign_btn = itemView.findViewById(R.id.campaign_btn)

            pollquestion = itemView.findViewById(R.id.tv_question)
            contestantone = itemView.findViewById(R.id.tv_option1)
            contestanttwo = itemView.findViewById(R.id.tv_option2)
            btncontestantone = itemView.findViewById(R.id.btn_contestantone1)
            btncontestanttwo = itemView.findViewById(R.id.btn_contestanttwo2)

            seekBar1 = itemView.findViewById(R.id.seek_bar1)
            seekBar2 = itemView.findViewById(R.id.seek_bar2)
            tvPercent1 = itemView.findViewById(R.id.tv_percent1)
            tvPercent2 = itemView.findViewById(R.id.tv_percent2)

            radioGroup = itemView.findViewById(R.id.radio_group)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder
    {

        val view = LayoutInflater.from(mContext).inflate(R.layout.for_post, parent, false)
            return ViewHolder(view)

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost!![position]


        if (post.getPostimage().isEmpty()) {
            holder.postImage.setImageResource(R.drawable.profile)
        } else {
            Picasso.get().load(post.getPostimage()).into(holder.postImage)
        }


        //description
        if (post.getDescription().equals("")) {
            holder.description.visibility = View.GONE

        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())

        }
        // 1
        if (post.getPollquestion().equals("")) {
            holder.pollquestion.visibility = View.GONE

        } else {
            holder.pollquestion.visibility = View.VISIBLE
            holder.pollquestion.setText(post.getPollquestion())
        }

        //2
        if (post.getContestantone().equals("")) {
            holder.contestantone.visibility = View.GONE

        } else {
            holder.contestantone.visibility = View.VISIBLE
            holder.contestantone.setText(post.getContestantone())
        }

        //3
        if (post.getContestanttwo().equals("")) {
            holder.contestanttwo.visibility = View.GONE

        } else {
            holder.contestanttwo.visibility = View.VISIBLE
            holder.contestanttwo.setText(post.getContestanttwo())
        }

        //

        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)
//        getTotalPolling(post.getPostid(), tvPercent1, tvPercent2)

        numberOfcontestantOne(post.getPostid(),holder.seekBar1 ,holder.seekBar2 , holder.tvPercent1,holder.tvPercent2)
        numberOfcontestantTwo(post.getPostid(),holder.seekBar1 ,holder.seekBar2 , holder.tvPercent1,holder.tvPercent2)

        calculatePecent(post.getPostid(), holder.tvPercent1,holder.tvPercent2,holder.seekBar1 ,holder.seekBar2)


        holder.likeButton.setOnClickListener {

            if (holder.likeButton.tag == "Like") {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()

//                val intent = Intent(mContext, MainActivity::class.java)
//                mContext.startActivity(intent)
            }
        }

        // share button  ---------(1)
        holder.sharebtn.setOnClickListener {

//                    val bitmapDrawable = BitmapDrawable()
//                    holder.postImage.drawable

//                    if (bitmapDrawable != null)
//                    {
//                        //post without image
//                        shareTextOnly(pollquestion = toString(),description = toString())
//                    }
//                    else
//                    {
//                        //post with image
//                        //convert image  to bitmap
//                        val bitmap = bitmapDrawable.bitmap
//                        shareImageAndText(bitmap)
//                    }

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "")

            mContext.startActivity(Intent.createChooser(intent, "Share To:"))

        }


        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }


        holder.campaign_btn.setOnClickListener {
            val intent = Intent(mContext, CompaignPage::class.java)
            intent.putExtra("postId", post.getPostid())
            intent.putExtra("publisherId", post.getPublisher())
            intent.putExtra("contestantone", post.getContestantone())
            intent.putExtra("contestanttwo", post.getContestanttwo())
            mContext.startActivity(intent)

        }

        holder.comments.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.userName.setOnClickListener {
            val intent = Intent(mContext, UserProfileActivity::class.java)
            intent.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intent)
        }

        holder.saveButton.setOnClickListener {
            if (holder.saveButton.tag == "Save") {
                FirebaseDatabase.getInstance().reference
                    .child("Saves").child(firebaseUser!!.uid)
                    .child(post.getPostid()).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Saves").child(firebaseUser!!.uid)
                    .child(post.getPostid()).removeValue()
            }
        }

        holder.seekBar1.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })

        holder.seekBar2.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })


        holder.btncontestantone.isChecked = UPdate("JP_One")
        holder.btncontestanttwo.isChecked = UPdate("JP_Two")

        holder.btncontestantone.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

                saveRadioButtonChecked("JP_One", p1)

            }

        })


        holder.btncontestanttwo.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p2: Boolean) {

                saveRadioButtonChecked("JP_Two", p2)

            }

        })

           holder.btncontestantone.setOnClickListener {

                       FirebaseDatabase.getInstance().reference
                           .child("ContestantOne")
                           .child(post.getPostid())
                           .child(firebaseUser!!.uid)
                           .setValue(true)
                       FirebaseDatabase.getInstance().reference
                           .child("ContestantTwo")
                           .child(post.getPostid())
                           .child(firebaseUser!!.uid)
                           .removeValue()

               }


        holder.btncontestanttwo.setOnClickListener {

                    FirebaseDatabase.getInstance().reference
                        .child("ContestantTwo")
                        .child(post.getPostid())
                        .child(firebaseUser!!.uid)
                        .setValue(true)
                    FirebaseDatabase.getInstance().reference
                        .child("ContestantOne")
                        .child(post.getPostid())
                        .child(firebaseUser!!.uid)
                        .removeValue()

            }






 /*       holder.btncontestantone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {



                if (flag2) {
                    // when flag two is true

                        if (count1 == 0){
                            count1++
                        }else{
                            count1 = 0
                        }

                    flag1 = true
                    flag2 = false
                    // calculate percentage
                    calculatePecent(holder.seekBar1, holder.seekBar2)
                    PollingSaveData(post.getPostid())

                }
            }
        })

        holder.btncontestanttwo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                if (count1 == 0){
                    count1++
                }else{
                    count1 = 0
                }

                if (flag1) {
                    // when flag two is true
                        if (count2 == 0){
                            count2++
                        }else{
                            count2 = 0
                        }


                    flag1 = false
                    flag2 = true
                    // calculate percentage

                    calculatePecent(holder.seekBar1, holder.seekBar2)
                    PollingSaveData(post.getPostid())

                }
            }
        })    */




        /*    if (view.getId()==R.id.rdb_1)
    {
        if(rdb_1.isChecked()) {
            party1++;
            Toast.makeText(this,"you vote for party1",Toast.LENGTH_LONG).show();
        }
        else party1--;
    }
    if (view.getId()==R.id.rdb_2)
    {
        if(rdb_2.isChecked())  {
            Toast.makeText(this,"you vote for party2",Toast.LENGTH_LONG).show();
            party2++;
        }
        else party2--;
    }       */





 /*       holder.btncontestantone.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                if (flag1) {
                    // when flag two is true
                    count1++

                }
                // calculate percentage
                calculatePecent(holder.seekBar1, holder.seekBar2)
                PollingSaveData(post.getPostid())
            }
        })

        holder.btncontestanttwo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                if (flag2) {
                    // when flag two is true
                    count2++

                }
                // calculate percentage
                calculatePecent(holder.seekBar1, holder.seekBar2)
                PollingSaveData(post.getPostid())
            }

        })      */

    }


    override fun getItemCount(): Int {
        return mPost.size
    }


    private fun shareImageAndText(bitmap: Bitmap) {

        //concatenate title and description to share
//        val shareBody = pollquestion + "\n" + description

        //first we will save this image in cache, get the saved image uid
        val uri = saveImageToShare(bitmap)

        //share intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
//        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        intent.setType("image/png")
        mContext.startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun saveImageToShare(bitmap: Bitmap): Uri? {
        val imageFolder = File(mContext.cacheDir,"images")
        var uri: Uri? = null

        try {
            imageFolder.mkdirs()     // create if not exists
            val file = File(imageFolder,"shared_image.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG,90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(mContext,"com.example.myapplication.fileprovider", file)
        }
        catch (e: Exception)
        {
            Toast.makeText(mContext, "" + e.message, Toast.LENGTH_SHORT).show()
        }
        return uri
    }

    private fun shareTextOnly(pollquestion: String, description: String) {
//concatenate title and description to share
        val shareBody = pollquestion + "\n" + description
//share intent

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here ")   //in case you share via an email app
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        mContext.startActivity(Intent.createChooser(intent, "Share To:"))   // message to show in share dialog

    }



        private fun numberOfLikes(likes: TextView, postid: String) {
            val LikesRef = FirebaseDatabase.getInstance().reference
                .child("Likes").child(postid)

            LikesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(pO: DataSnapshot) {
                    if (pO.exists()) {
                        likes.text = pO.childrenCount.toString() + " likes"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    private fun numberOfcontestantOne(postid: String, seekBar1: SeekBar , seekBar2: SeekBar ,  tvPercent1 : TextView, tvPercent2: TextView) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("ContestantOne").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {

 //                   tvPercent1.text = pO.childrenCount.toString() + "%"

                   var count1 = pO.childrenCount.toString()

//                    seekBar1.progress = count1

                    val commentsRef = FirebaseDatabase.getInstance().reference
                        .child("Polling")
                        .child(postid)
                    val commentsMap = HashMap<String, Any>()

                    commentsMap["tvPercent1"] = count1

                    commentsRef.updateChildren(commentsMap)

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun numberOfcontestantTwo(postid: String, seekBar1: SeekBar , seekBar2:SeekBar , tvPercent1 : TextView, tvPercent2: TextView) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("ContestantTwo").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {

 //                   tvPercent2.text = pO.childrenCount.toString() + "%"

                   var  count2 = pO.childrenCount.toString()

                    val commentsRef = FirebaseDatabase.getInstance().reference
                        .child("Polling")
                        .child(postid)
                    val commentsMap = HashMap<String, Any>()

                    commentsMap["tvPercent2"] = count2

                    commentsRef.updateChildren(commentsMap)




/*                    val total = (count1 + count2).toDouble()
                    val percent1 = count1 / total * 100
                    val percent2 = count2 / total * 100

                    tvPercent1.text = String.format("%.0f%%", percent1)
                    seekBar1.progress = percent1.toInt()
                    tvPercent2.text = String.format("%.0f%%", percent2)
                    seekBar2.progress = percent2.toInt()                         */
//                    calculatePecent(tvPercent1, tvPercent2)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }










    private fun calculatePecent(postid: String,tvPercent1 : TextView, tvPercent2: TextView , seekBar1: SeekBar,seekBar2: SeekBar) {
        // calculate total

        val pollingRef = FirebaseDatabase.getInstance().reference
            .child("Polling")
            .child(postid)

        pollingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {

                    var A  = pO
                        .child("tvPercent1").value.toString()

                    if (A == "null"){
                        A = 0.toString()
                    }

                   var count1 = A!!.toInt()

                    var B = pO
                        .child("tvPercent2").value.toString()

                    if (B == "null"){
                        B = 0.toString()
                    }

                    var count2 = B!!.toInt()

                    val total = (count1 + count2).toDouble()
                    val percent1 = count1 / total * 100
                    val percent2 = count2 / total * 100
                    tvPercent1.text = String.format("%.0f%%", percent1)
                    seekBar1.progress = percent1.toInt()
                    tvPercent2.text = String.format("%.0f%%", percent2)
                    seekBar2.progress = percent2.toInt()


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

/*    private fun PollingSaveData(postid: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Polling")
            .child(postid)
        val commentsMap = HashMap<String, Any>()

        commentsMap["tvPercent1"] = tvPercent1!!.text.toString()
        commentsMap["tvPercent2"] = tvPercent2!!.text.toString()

        commentsRef.child(firebaseUser!!.uid).updateChildren(commentsMap)

    }    */


    private fun getTotalPolling(postid: String, tvPercent1: TextView, tvPercent2: TextView) {
            val pollingRef = FirebaseDatabase.getInstance().reference
                .child("Polling")
                .child(postid)

            pollingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(pO: DataSnapshot) {
                    if (pO.exists()) {

                        tvPercent1.text = pO
                            .child(firebaseUser!!.uid)
                            .child("tvPercent1").value.toString()

                        tvPercent2.text = pO
                            .child(firebaseUser!!.uid)
                            .child("tvPercent2").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }


        private fun getTotalComments(comments: TextView, postid: String) {
            val commentsRef = FirebaseDatabase.getInstance().reference
                .child("Comments").child(postid)

            commentsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(pO: DataSnapshot) {
                    if (pO.exists()) {
                        comments.text = "view all " + pO.childrenCount.toString() + " comments"
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun isLikes(postid: String, likeButton: ImageView) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val LikesRef = FirebaseDatabase.getInstance().reference
                .child("Likes")
                .child(postid)
            LikesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(pO: DataSnapshot) {
                    if (pO.child(firebaseUser!!.uid).exists()) {
                        likeButton.setImageResource(R.drawable.heart_clicked)
                        likeButton.tag = "Liked"
                    } else {
                        likeButton.setImageResource(R.drawable.heart_not_clicked)
                        likeButton.tag = "Like"
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    private fun contestantOne(postid: String, btncontestantone: RadioButton){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ContestantOne = FirebaseDatabase.getInstance().reference.child("ContestantOne").child(postid)
        ContestantOne.addValueEventListener(object  : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.child(firebaseUser!!.uid).exists()) {

                    btncontestantone.tag = "Liked"
                }else{

                    btncontestantone.tag = "ContestantOne"
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun contestantTwo(postid: String, btncontestantTwo: RadioButton){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ContestantOne = FirebaseDatabase.getInstance().reference.child("ContestantTwo").child(postid)
        ContestantOne.addValueEventListener(object  : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.child(firebaseUser!!.uid).exists()) {

                    btncontestantTwo.tag = "Liked"
                }else{

                    btncontestantTwo.tag = "ContestantTwo"
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

        private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {

            val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot)
                {
                    if (snapshot.exists())
                    {
                        val user = snapshot.getValue<User>(User::class.java)

                        if (user!!.getImage().isEmpty()) {
                            profileImage.setImageResource(R.drawable.profile)
                        } else {
                            Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                        }

//                      Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                        userName.text = user!!.getUsername()
                        publisher.text = user!!.getFullname()

                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        private fun checkSavedStatus(postid: String, imageView: ImageView) {
            val saveRef = FirebaseDatabase.getInstance().reference
                .child("Saves")
                .child(firebaseUser!!.uid)

            saveRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(postid).exists()) {
                        imageView.setImageResource(R.drawable.save_large_icon)
                        imageView.tag = "Saved"
                    } else {
                        imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                        imageView.tag = "Save"
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    private fun saveRadioButtonChecked(key: String,value: Boolean){

        val sharedPreference = mContext.getSharedPreferences("JP_TECH", MODE_PRIVATE)
        val editor = sharedPreference.edit()

        editor.putBoolean(key,value)
        editor.apply()

    }

    private fun UPdate(key: String): Boolean {

        val sharedPreference = mContext.getSharedPreferences("JP_TECH", MODE_PRIVATE)
             return sharedPreference.getBoolean(key, false)

    }


}
