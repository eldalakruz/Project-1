package com.example.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CommentsActivity
import com.example.myapplication.Model.AllPost
import com.example.myapplication.Model.Newpost
import com.example.myapplication.Model.User
import com.example.myapplication.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.roundToInt

class NewpostAdapter(private val mContext: Context,
                    private val mPost: ArrayList<Newpost>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var firebaseUser:FirebaseUser? =null

    companion object{
        //TAG
        private const val TAG ="PRODUCT_TAG"
        private const val VIEW_TYPE_VIDEO = 0
        private const val VIEW_TYPE_CONTENT = 1
        private const val VIEW_TYPE_AD = 2

    }


    private var item_type = 0

    inner class HolderProduct( itemView: View): RecyclerView.ViewHolder(itemView) {

        val profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_newPost)
        val postImage: ImageView = itemView.findViewById(R.id.post_image_home2)
        val likeButton: ImageView = itemView.findViewById(R.id.post_image_like_button)
        val commentButton: ImageView = itemView.findViewById(R.id.post_image_comment_button)
        val saveButton: ImageView = itemView.findViewById(R.id.post_save_comment_button)
        val userName: TextView = itemView.findViewById(R.id.user_name_search)
        val likesuser: TextView = itemView.findViewById(R.id.likes_user)
        val publisheruser: TextView = itemView.findViewById(R.id.publisher_user)
        val descriptionuser: TextView = itemView.findViewById(R.id.description_user)
        val commentsuser: TextView = itemView.findViewById(R.id.comments_user)

    }

    inner class VideoPosts(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {


        val profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_newPost)
        val videoView: VideoView = itemView.findViewById(R.id.post_image_home2)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val likeButton: ImageView = itemView.findViewById(R.id.post_image_like_button)
        val commentButton: ImageView = itemView.findViewById(R.id.post_image_comment_button)
        val saveButton: ImageView = itemView.findViewById(R.id.post_save_comment_button)
        val userName: TextView = itemView.findViewById(R.id.user_name_search)
        val likesuser: TextView = itemView.findViewById(R.id.likes_user)
        val publisheruser: TextView = itemView.findViewById(R.id.publisher_user)
        val descriptionuser: TextView = itemView.findViewById(R.id.description_user)
        val commentsuser: TextView = itemView.findViewById(R.id.comments_user)


    }


    inner class HolderNativeAd(itemView: View): RecyclerView.ViewHolder(itemView){

        val ad_app_icon :ImageView =itemView.findViewById(R.id.ad_app_icon)
        val ad_headline:TextView=itemView.findViewById(R.id.ad_headline)
        val ad_advertiser:TextView=itemView.findViewById(R.id.ad_advertiser)
        val ad_stars: RatingBar =itemView.findViewById(R.id.ad_stars)
        val ad_body:TextView=itemView.findViewById(R.id.ad_body)
        val media_view: MediaView =itemView.findViewById(R.id.media_view)
        val ad_price:TextView=itemView.findViewById(R.id.ad_price)
        val ad_store:TextView=itemView.findViewById(R.id.ad_store)
        val ad_call_to_action: Button =itemView.findViewById(R.id.ad_call_to_action)
        val native_Ad_View: NativeAdView =itemView.findViewById(R.id.nativeAdView)

    }

    override fun getItemCount(): Int {

        if (mPost.size > 0) {
            val size =  mPost.size + (mPost.size / 5).toDouble().roundToInt()
            Log.d("TAG","getItemCount_${size}")
            return size
        }
        return mPost.size
    }

    override fun getItemViewType(position: Int): Int {
        //logic to display Native Ad between content
        if ((position + 1) % 5 == 0){
            //after 5 items .show native ad
            item_type = VIEW_TYPE_AD
        }
        else{
            val index = position - (position / 5).toDouble().roundToInt()
            if (mPost[index].getPostimage() == ""){

                item_type = VIEW_TYPE_VIDEO
            }else{
                //return the view type VIEW_TYPE_CONTENT to show content
                item_type = VIEW_TYPE_CONTENT

            }
        }
        return item_type
    }


override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder
{

    val view :View

      if (viewType == VIEW_TYPE_CONTENT){
        view = LayoutInflater.from(mContext).inflate(R.layout.new_post,parent,false)
          return HolderProduct(view)

      }
      else if(viewType == VIEW_TYPE_VIDEO){
          view = LayoutInflater.from(mContext).inflate(R.layout.new_post_video_page_two, parent, false)
          return VideoPosts(view)

      } else{

          view = LayoutInflater.from(mContext).inflate(R.layout.row_native_ad,parent,false)
         return HolderNativeAd(view)

      }

}


override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    firebaseUser = FirebaseAuth.getInstance().currentUser

      if (item_type == VIEW_TYPE_CONTENT) {

           val post = mPost[position]

           holder as HolderProduct

          //    Picasso.get().load(post.getPostimage()).into(holder.postImage)
          if (post.getPostimage().isEmpty()) {
              holder.postImage.setImageResource(R.drawable.profile)
          } else {
              Picasso.get().load(post.getPostimage()).into(holder.postImage)
          }

          publisherInfo(holder.profileImage, holder.userName, holder.publisheruser, post.getPublisher())
          isLikes(post.getPostid(), holder.likeButton)
          numberOfLikes(holder.likesuser, post.getPostid())
          getTotalComments(holder.commentsuser, post.getPostid())
          checkSavedStatus(post.getPostid(), holder.saveButton)

          if (post.getDescription() == "") {
              holder.descriptionuser.visibility = View.GONE

          } else {
              holder.descriptionuser.visibility = View.VISIBLE
              holder.descriptionuser.text = post.getDescription()
          }

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

          holder.commentsuser.setOnClickListener {
              val intentComment = Intent(mContext, CommentsActivity::class.java)
              intentComment.putExtra("postId", post.getPostid())
              intentComment.putExtra("publisherId", post.getPublisher())
              mContext.startActivity(intentComment)
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

        }

    if (item_type == VIEW_TYPE_VIDEO){

        val post = mPost[position]

        holder as VideoPosts

        setVideoUrl(post, holder)
        publisherInfo(holder.profileImage, holder.userName, holder.publisheruser, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton)
        numberOfLikes(holder.likesuser, post.getPostid())
        getTotalComments(holder.commentsuser, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)

        if (post.getDescription() == "") {
            holder.descriptionuser.visibility = View.GONE

        } else {
            holder.descriptionuser.visibility = View.VISIBLE
            holder.descriptionuser.text = post.getDescription()
        }

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

        holder.commentsuser.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
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

    }



          if (item_type == VIEW_TYPE_AD) {


            val adLoader  = AdLoader.Builder(mContext ,mContext.getString(R.string.native_ad_id_test))
                .forNativeAd { nativeAd ->
                    Log.d(TAG, "onNativeAdLoaded: ")

                    //Ad is loaded,show it

                    //instance of our HolderNativeAd to access UI views of row_Native_ad.xml
                    val holderNativeAd = holder as HolderNativeAd
                    displayNativeAd(holderNativeAd,nativeAd)
                }
                .withAdListener(object : AdListener() {

                    override fun onAdClicked() {
                        super.onAdClicked()
                        Log.d(TAG, "onAdClicked: ")
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        Log.d(TAG, "onAdClosed: ")
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        Log.d(TAG, "onAdFailedToLoad: ${p0.message}")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        Log.d(TAG, "onAdImpression: ")
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d(TAG, "onAdLoaded: ")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Log.d(TAG, "onAdOpened: ")
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder().build()).build()

            adLoader.loadAd(AdRequest.Builder().build())

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
        val saveRef = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)

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


    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {
      val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())

                {
                    val user = snapshot.getValue(User::class.java)

                   // Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
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

    private fun displayNativeAd(holderNativeAd: NewpostAdapter.HolderNativeAd, nativeAd: NativeAd) {
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

    private fun setVideoUrl(post: Newpost, holder: VideoPosts) {

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


}
