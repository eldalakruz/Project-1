package com.example.myapplication.Model

class Newpost {


    private var postid : String = ""
    private var publisher : String = ""
    private var description : String = ""
    private var postimage : String = ""
    var videoUri: String = ""



    constructor()

    constructor(
        videoUri: String,
        postimage: String,
        postid: String,
        publisher: String,
        description: String)

    {
        this.videoUri = videoUri
        this.postimage = postimage
        this.postid = postid
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

