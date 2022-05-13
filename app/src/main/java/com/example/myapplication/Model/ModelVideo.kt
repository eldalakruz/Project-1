package com.example.myapplication.Model

class ModelVideo
{
    //variables, use names as in firebase
    var id: String? = null
    var title: String? = null
    var timestamp: String? = null
    var videoUri: String? = null
    private var publisher : String = ""
    private var postid : String = ""

    //constructor
    constructor(){
        //firebase requires empty constructor

    }

    constructor(id: String? , title: String?, timestamp: String?, videoUri: String?, publisher: String,  postid: String) {
        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.videoUri = videoUri
        this.publisher = publisher
        this.postid = postid

    }

    fun getPublisher() : String{
        return  publisher
    }

    fun getpostid() : String? {
        return  postid
    }



}