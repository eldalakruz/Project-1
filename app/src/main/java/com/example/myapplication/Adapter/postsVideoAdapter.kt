package com.example.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CommentsActivity
import com.example.myapplication.CompaignPage
import com.example.myapplication.Model.AllPost
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.Post
import com.example.myapplication.Model.User
import com.example.myapplication.R
import com.example.myapplication.UserProfileActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.roundToInt

class postsVideoAdapter(private val mContext: Context,
                        private var mPost: ArrayList<AllPost>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {


    companion object {

        private const val VIEW_TYPE_VIDEO = 0
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_AD = 2
        const val PREFIX = "https://pollplay.page.link"

    }


    private var item_type = 0

    private var firebaseUser: FirebaseUser? = null


    inner class Posts(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_post)
        var likeButton: ImageView = itemView.findViewById(R.id.post_image_like_btn)
        var commentButton: ImageView = itemView.findViewById(R.id.post_image_comment_btn)
        var saveButton: ImageView = itemView.findViewById(R.id.post_save_comment_btn)
        var sharebtn: ImageView = itemView.findViewById(R.id.post_video_share_btn)

        var userName: TextView = itemView.findViewById(R.id.user_name_post)
        var likes: TextView = itemView.findViewById(R.id.likes)
        var publisher: TextView = itemView.findViewById(R.id.publisher)
        var description: TextView = itemView.findViewById(R.id.description)
        var comments: TextView = itemView.findViewById(R.id.comments)
        var campaign_btn: TextView = itemView.findViewById(R.id.campaign_btn)

        var pollquestion: TextView = itemView.findViewById(R.id.tv_question)
        var contestantone: TextView = itemView.findViewById(R.id.tv_option1)
        var contestanttwo: TextView = itemView.findViewById(R.id.tv_option2)

        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        var videoView: VideoView = itemView.findViewById(R.id.video_post)

        var seekBar1: SeekBar = itemView.findViewById(R.id.seek_bar1)
        var seekBar2: SeekBar = itemView.findViewById(R.id.seek_bar2)
        var tvPercent1: TextView = itemView.findViewById(R.id.tv_percent1)
        var tvPercent2: TextView = itemView.findViewById(R.id.tv_percent2)
        var btncontestantone: RadioButton = itemView.findViewById(R.id.btn_contestantone1)
        var btncontestanttwo: RadioButton = itemView.findViewById(R.id.btn_contestanttwo2)


    }


    inner class ImagePosts(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_post)
        var postImage: ImageView = itemView.findViewById(R.id.post_image_home)
        var likeButton: ImageView = itemView.findViewById(R.id.post_image_like_btn)
        var commentButton: ImageView = itemView.findViewById(R.id.post_image_comment_btn)
        var saveButton: ImageView = itemView.findViewById(R.id.post_save_comment_btn)
        var sharebtn: ImageView = itemView.findViewById(R.id.post_image_share_btn)

        var userName: TextView = itemView.findViewById(R.id.user_name_post)
        var likes: TextView = itemView.findViewById(R.id.likes)
        var publisher: TextView = itemView.findViewById(R.id.publisher)
        var description: TextView = itemView.findViewById(R.id.description)
        var comments: TextView = itemView.findViewById(R.id.comments)
        var campaign_btn: TextView = itemView.findViewById(R.id.campaign_btn)

        var pollquestion: TextView = itemView.findViewById(R.id.tv_question)
        var contestantone: TextView = itemView.findViewById(R.id.tv_option1)
        var contestanttwo: TextView = itemView.findViewById(R.id.tv_option2)

        var seekBar1: SeekBar = itemView.findViewById(R.id.seek_bar1)
        var seekBar2: SeekBar = itemView.findViewById(R.id.seek_bar2)
        var tvPercent1: TextView = itemView.findViewById(R.id.tv_percent1)
        var tvPercent2: TextView = itemView.findViewById(R.id.tv_percent2)
        var btncontestantone: RadioButton = itemView.findViewById(R.id.btn_contestantone1)
        var btncontestanttwo: RadioButton = itemView.findViewById(R.id.btn_contestanttwo2)

    }

    inner class HolderNativeAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //init UI Views

        val ad_app_icon : ImageView = itemView.findViewById(R.id.ad_app_icon)
        val ad_headline : TextView = itemView.findViewById(R.id.ad_headline)
        val ad_advertiser : TextView = itemView.findViewById(R.id.ad_advertiser)
        val ad_stars : RatingBar = itemView.findViewById(R.id.ad_stars)
        val ad_body : TextView = itemView.findViewById(R.id.ad_body)
        val media_view : MediaView = itemView.findViewById(R.id.media_view)
        val ad_price : TextView = itemView.findViewById(R.id.ad_price)
        val ad_store : TextView = itemView.findViewById(R.id.ad_store)
        val ad_call_to_action : Button = itemView.findViewById(R.id.ad_call_to_action)
        val native_Ad_View : NativeAdView = itemView.findViewById(R.id.nativeAdView)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View

        if (viewType == VIEW_TYPE_VIDEO) {

            view = LayoutInflater.from(mContext).inflate(R.layout.video_posts, parent, false)
            return Posts(view)
        }
        else if (viewType == VIEW_TYPE_IMAGE){

            view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
            return ImagePosts(view)
        }

        else {

            view = LayoutInflater.from(mContext).inflate(R.layout.row_native_ad,parent,false)
            return HolderNativeAd(view)
        }
    }

    override fun getItemViewType(position: Int): Int {

        if ((position + 1) % 5 == 0){
            item_type = VIEW_TYPE_AD
        Log.d("TAG", "getItemViewType_AD_$position")

        } else  {

            Log.d("TAG","getItemViewType____$position")

            val index = position - (position / 5).toDouble().roundToInt()
            if (mPost[index].getPostimage() == "") {
                item_type = VIEW_TYPE_VIDEO
 //               Log.d("TAG", "getItemViewType_VIDEO_$position")
            } else {
                item_type = VIEW_TYPE_IMAGE
                Log.d("TAG", "getItemViewType_IMAGE_$position")
                Log.d("TAG", "getItemViewType_IMAGE_index_$index")
            }
        }
        return item_type
    }

    override fun getItemCount(): Int {

        if (mPost.size > 0) {
        val size =  mPost.size + (mPost.size / 5).toDouble().roundToInt()
            Log.d("TAG","getItemCount_${size}")
            return size
        } else
        return mPost.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        when (item_type) {

            VIEW_TYPE_VIDEO -> {

                val pos = position - (position / 5).toDouble().roundToInt()

                val post = mPost[pos]

                Log.d("TAG", "onBindViewHolder_VIDEO_position_${position}")



                (holder as Posts)
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

                publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())
         //       setVideoUrl(post, holder)
                isLikes(post.getPostid(), holder.likeButton)
                numberOfLikes(holder.likes, post.getPostid())
                getTotalComments(holder.comments, post.getPostid())
                checkSavedStatus(post.getPostid(), holder.saveButton)
                calculatePecent(post.getPostid(),holder.tvPercent1, holder.tvPercent2, holder.seekBar1,holder.seekBar2)
                numberOfcontestantOne(post.getPostid())
                numberOfcontestantTwo(post.getPostid())

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
                    }
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

                holder.sharebtn.setOnClickListener {
                    // Using the function we created to generate Sharing Link.
                    generatesharinglinkForVideo(
                        deepLink = "${PREFIX}/post/${post.getPostid()}".toUri(),
//                    previewImageLink = "${post.getVideoUri()}".toUri(),
                        forDescription = "${post.getDescription().toUri()}")

                    { generatedLink ->
                        // Use this generated Link to share via Intent
                        shareDeepLink(generatedLink)
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

                holder.btncontestantone.setOnCheckedChangeListener { p0, p1 ->
                    saveRadioButtonChecked("JP_One", p1)
                }


                holder.btncontestanttwo.setOnCheckedChangeListener { p0, p2 ->
                    saveRadioButtonChecked("JP_Two", p2)
                }

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

            }

            VIEW_TYPE_IMAGE -> {


                val pos = position - (position / 5).toDouble().roundToInt()

                val post = mPost[pos]

                Log.d("TAG", "onBindViewHolder_IMAGE_position_${position}")
//                Log.d("TAG", "onBindViewHolder_IMAGE_pos_${pos}")

                (holder as ImagePosts)

                if (post.getPostimage().isEmpty()) {
                    holder.postImage.setImageResource(R.drawable.profile)
                } else {
                    Picasso.get().load(post.getPostimage()).into(holder.postImage)
                }


                if (post.getDescription() == "") {
                    holder.description.visibility = View.GONE


                } else {
                    holder.description.visibility = View.VISIBLE
                    holder.description.text = post.getDescription()

                }
                // 1
                if (post.getPollquestion() == "") {
                    holder.pollquestion.visibility = View.GONE

                } else {
                    holder.pollquestion.visibility = View.VISIBLE
                    holder.pollquestion.text = post.getPollquestion()
                }

                //2
                if (post.getContestantone() == "") {
                    holder.contestantone.visibility = View.GONE

                } else {
                    holder.contestantone.visibility = View.VISIBLE
                    holder.contestantone.text = post.getContestantone()
                }

                //3
                if (post.getContestanttwo() == "") {
                    holder.contestanttwo.visibility = View.GONE

                } else {
                    holder.contestanttwo.visibility = View.VISIBLE
                    holder.contestanttwo.text = post.getContestanttwo()
                }


                publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())
                isLikes(post.getPostid(), holder.likeButton)
                numberOfLikes(holder.likes, post.getPostid())
                getTotalComments(holder.comments, post.getPostid())
                checkSavedStatus(post.getPostid(), holder.saveButton)
                calculatePecent(post.getPostid(),holder.tvPercent1,holder.tvPercent2,holder.seekBar1,holder.seekBar2)
                numberOfcontestantOne(post.getPostid())
                numberOfcontestantTwo(post.getPostid())


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

                    }
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


                holder.sharebtn.setOnClickListener {


                    // Using the function we created to generate Sharing Link.
                    generatesharinglink(
                        deepLink = "${PREFIX}/post/${post.getPostid()}".toUri(),
                        forShare = "${post.getPostimage()}".toUri(),
                        forDescription = "${post.getDescription().toUri()}"
                    )
                    { generatedLink ->
                        // Use this generated Link to share via Intent
                        shareDeepLink(generatedLink)
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

                holder.btncontestantone.setOnCheckedChangeListener { p0, p1 ->
                    saveRadioButtonChecked(
                        "JP_One",
                        p1
                    )
                }


                holder.btncontestanttwo.setOnCheckedChangeListener { p0, p2 ->
                    saveRadioButtonChecked(
                        "JP_Two",
                        p2
                    )
                }

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
            }

            // for Adds

            VIEW_TYPE_AD -> {

                val adLoader = AdLoader.Builder(mContext, mContext.getString(R.string.native_ad_id_test)).forNativeAd { nativeAd ->

                            //Ad is loaded,show it
                            //instance of our HolderNativeAd to access UI views of row_Native_ad.xml
                            val holderNativeAd = holder as HolderNativeAd
                            displayNativeAd(holderNativeAd, nativeAd)
                        }
                        .withAdListener(object : AdListener() {

                            override fun onAdFailedToLoad(p0: LoadAdError) {
                                super.onAdFailedToLoad(p0)

                            }

                        })
                        .withNativeAdOptions(NativeAdOptions.Builder().build()).build()

                adLoader.loadAd(AdRequest.Builder().build())


            }
        }
    }



    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String)
    {
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

                    userName.text = user!!.getUsername()
                    publisher.text = user!!.getFullname()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private fun setVideoUrl(post: AllPost, holder: Posts) {

        //show progress
        holder.progressBar.visibility = View.VISIBLE

        //get video uri
        val videoUrl: String? = post.videoUri

//        Log.e("sample","test:$videoUrl")

        //MediaController for play/pause/time etc
        val mediaController = MediaController(mContext)
        mediaController.setAnchorView(holder.videoView)
        val videoUri = Uri.parse(videoUrl)

        holder.videoView.setMediaController(mediaController)
        holder.videoView.setVideoURI(videoUri)
        holder.videoView.requestFocus()

        holder.videoView.setOnPreparedListener {mediaPlayer ->
            //video is prepared to play
            mediaPlayer.start()
        }
        holder.videoView.setOnInfoListener(MediaPlayer.OnInfoListener{ mp, what, extra->
            //check if buffering/rendering etc
            when(what){
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ->{
                    //rendering started
                    holder.progressBar.visibility = View.GONE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START ->{
                    //buffering started
                    holder.progressBar.visibility = View.VISIBLE
                    return@OnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END ->{
                    //buffering ended
                    holder.progressBar.visibility = View.GONE
                    return@OnInfoListener true
                }
            }

            false
        })

        holder.videoView.setOnCompletionListener {mediaPlayer ->
            //restart video when completed | loop video
            mediaPlayer.start()

        }

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

    private fun numberOfLikes(likes: TextView, postid: String) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {
                    likes.text = pO.childrenCount.toString() + " likes"
                } else {
                    likes.text = pO.childrenCount.toString() + " likes"
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


    fun generatesharinglink(forDescription: String, forShare: Uri, deepLink: Uri, getShareableLink: (String) -> Unit = {})
    {

        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = deepLink
            domainUriPrefix = "https://pollplay.page.link"


            socialMetaTagParameters {
                title = "Poll Play"
                description = forDescription
                imageUrl =  forShare
            }

            androidParameters {
                build()
            }

            buildShortDynamicLink()

        }.addOnSuccessListener { dynamicLink ->

            getShareableLink.invoke(dynamicLink.shortLink.toString())
        }.addOnFailureListener {


        }
    }


    fun generatesharinglinkForVideo(deepLink: Uri, forDescription: String, getShareableLink: (String) -> Unit = {}) {

        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            // What is this link parameter? You will get to know when we will actually use this function.
            link = deepLink

            // [domainUriPrefix] will be the domain name you added when setting up Dynamic Links at Firebase Console.
            // You can find it in the Dynamic Links dashboard.
            domainUriPrefix = PREFIX

            // Pass your preview Image Link here;
            socialMetaTagParameters {
                title = "Poll Play"
                description = forDescription

            }

            androidParameters {
                build()
            }
            buildShortDynamicLink()

        }.also {
            it.addOnSuccessListener { dynamicLink ->

                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {

            }
        }
    }


    fun shareDeepLink(deepLink: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "You have been shared an amazing meme, check it out ->")
        intent.putExtra(Intent.EXTRA_TEXT, deepLink)
        mContext.startActivity(Intent.createChooser(intent, "Share To:"))

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


    private fun numberOfcontestantOne(postid: String) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("ContestantOne").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {

                    var count1 = pO.childrenCount.toString()

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


    private fun numberOfcontestantTwo(postid: String) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("ContestantTwo").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {

                    var  count2 = pO.childrenCount.toString()

                    val commentsRef = FirebaseDatabase.getInstance().reference
                        .child("Polling")
                        .child(postid)
                    val commentsMap = HashMap<String, Any>()

                    commentsMap["tvPercent2"] = count2

                    commentsRef.updateChildren(commentsMap)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun saveRadioButtonChecked(key: String,value: Boolean){

        val sharedPreference = mContext.getSharedPreferences("JP_TECH", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        editor.putBoolean(key,value)
        editor.apply()

    }

    private fun UPdate(key: String): Boolean {

        val sharedPreference = mContext.getSharedPreferences("JP_TECH", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean(key, false)

    }


    // for Adds

    private fun displayNativeAd(holderNativeAd: HolderNativeAd, nativeAd: NativeAd) {
        /*------Get Ad assets from the NativeAd Object-----*/

        val headLine = nativeAd.headline
        val body  = nativeAd.body
        val callToAction = nativeAd.callToAction
        val icon = nativeAd.icon
        val price = nativeAd.price
        val store = nativeAd.store
        val starRating = nativeAd.starRating
        val advertiser = nativeAd.advertiser
        val mediaContent = nativeAd.mediaContent

        /*-----Same assets aren't guaranteed to be in every nativeAd,so we need to check before displaying item-----*/

        if (headLine==null){
            //no content ,hide view
            holderNativeAd.ad_headline.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_headline.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_headline.text=headLine

        }

        if (body==null){
            //no content ,hide view
            holderNativeAd.ad_body.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_body.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_body.text=body

        }


        if (icon == null){
            //no content ,hide view
            holderNativeAd.ad_app_icon.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_app_icon.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_app_icon.setImageDrawable(icon.drawable)

        }
        if (starRating == null){
            //no content ,hide view
            holderNativeAd.ad_stars.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_stars.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_stars.rating=starRating.toFloat()

        }
        if (price==null){
            //no content ,hide view
            holderNativeAd.ad_price.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_price.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_price.text=price

        }
        if (store==null){
            //no content ,hide view
            holderNativeAd.ad_store.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_store.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_store.text=store

        }
        if (advertiser==null){
            //no content ,hide view
            holderNativeAd.ad_advertiser.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_advertiser.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_advertiser.text=advertiser

        }
        if (mediaContent==null){
            //no content ,hide view
            holderNativeAd.media_view.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.media_view.visibility= View.VISIBLE
            // set data
            holderNativeAd.media_view.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            holderNativeAd.media_view.setMediaContent(mediaContent)

        }
        if (callToAction==null){
            //no content ,hide view
            holderNativeAd.ad_call_to_action.visibility = View.INVISIBLE
        }
        else{
            //have content ,show view
            holderNativeAd.ad_call_to_action.visibility= View.VISIBLE
            // set data
            holderNativeAd.ad_call_to_action.text=callToAction
            //handle ad button click
            holderNativeAd.native_Ad_View.callToActionView=holderNativeAd.ad_call_to_action

        }
        // add nativeAd  the NativeView
        holderNativeAd.native_Ad_View.setNativeAd(nativeAd)
    }

}


