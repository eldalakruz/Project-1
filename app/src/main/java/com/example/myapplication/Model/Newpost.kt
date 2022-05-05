package com.example.myapplication.Model

import android.accounts.AuthenticatorDescription

class Newpost {







    private var postid : String = ""
    private var postimage : String = ""
    private var publisher : String = ""
    private var description : String = ""

     var icon :Int = 0

    constructor()


    constructor(

        postid: String,
        postimage: String,
        publisher: String,
        description: String,

        icon : Int

        )
    {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description

    }
    fun getPostid() : String{
        return  postid
    }

    fun getPostimage() : String{
        return  postimage
    }

    fun getPublisher() : String{
        return  publisher
    }

    fun getDescription() : String{
        return  description
    }

    fun setPostid(postid: String){
        this.postid = postid
    }

    fun setPostimage(postimage: String){
        this.postimage = postimage
    }

    fun setPublisger(publisher: String){
        this.publisher = publisher
    }

    fun setDescription(description: String){
        this.description = description
    }

}

