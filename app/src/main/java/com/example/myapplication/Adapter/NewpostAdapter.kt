package com.example.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

class NewpostAdapter(
    val context: Context,
    val Post: ArrayList<Newpost>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var firebaseUser:FirebaseUser? =null

    companion object{
        //TAG
        private const val TAG ="PRODUCT_TAG"

        //there will be 2 view type 1 for the actual content and 2 for the native ad

        private const val VIEW_TYPE_CONTENT =0
        private const val VIEW_TYPE_AD=1

    }


override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder
{

    val view :View

      if (viewType == VIEW_TYPE_CONTENT){
        view = LayoutInflater.from(context).inflate(R.layout.new_post,parent,false)
          return HolderProduct(view)
      }

      else{
          //inflate/return row_native_ad.xml
          view = LayoutInflater.from(context).inflate(R.layout.row_native_ad,parent,false)
         return HolderNativeAd(view)

      }

}


override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    firebaseUser = FirebaseAuth.getInstance().currentUser

      if (getItemViewType(position)== VIEW_TYPE_CONTENT) {

           val post = Post[position]

            val holderProduct =holder as HolderProduct

            Picasso.get().load(post.getPostimage()).into(holderProduct.postImage)

            publisherInfo(
                holderProduct.profileImage,
                holderProduct.userName,
                holderProduct.publisheruser,
                    post.getPublisher()
            )

        }

          if (getItemViewType(position)== VIEW_TYPE_AD) {


            val adLoader  = AdLoader.Builder(context ,context.getString(R.string.native_ad_id_test))
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



    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherID: String) {
      val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())

                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
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


    override fun getItemCount(): Int {
    return  Post.size
}

    override fun getItemViewType(position: Int): Int {
        //logic to display Native Ad between content
        if (position % 5 == 0){
            //after 5 items .show native ad
            return VIEW_TYPE_AD
        }
        else{
            //return the view type VIEW_TYPE_CONTENT to show content
            return  VIEW_TYPE_CONTENT
        }

    }



    inner class HolderProduct( itemView: View): RecyclerView.ViewHolder(itemView) {


       val profileImage: CircleImageView
       val postImage: ImageView
       val likeButton: ImageView
       val commentButton: ImageView
       val saveButton: ImageView
       val userName: TextView
       val likesuser: TextView
       val publisheruser: TextView
       val descriptionuser: TextView
       val commentsuser: TextView

       init {
           profileImage = itemView.findViewById(R.id.user_profile_image_newPost)
           postImage = itemView.findViewById(R.id.post_image_home2)
           likeButton = itemView.findViewById(R.id.post_image_like_button)
           commentButton = itemView.findViewById(R.id.post_image_comment_button)
           saveButton = itemView.findViewById(R.id.post_save_comment_button)
           userName = itemView.findViewById(R.id.user_name_search)
           likesuser = itemView.findViewById(R.id.likes_user)
           publisheruser = itemView.findViewById(R.id.publisher_user)
           descriptionuser = itemView.findViewById(R.id.description_user)
           commentsuser = itemView.findViewById(R.id.comments_user)
       }


   }
    //ViewHolder class for row_native_ad.xml

    inner class HolderNativeAd(itemView: View): RecyclerView.ViewHolder(itemView){
        //init UI Views
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

}
