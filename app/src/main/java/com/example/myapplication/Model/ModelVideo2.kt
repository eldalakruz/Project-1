package com.example.myapplication.Model

class ModelVideo2
{
    //variables, use names as in firebase
    var id2: String? = null
    var title2: String? = null
    var timestamp2: String? = null
    var videoUri2: String? = null
    private var publisher : String = ""
    private var postid : String = ""

    //constructor
    constructor(){
        //firebase requires empty constructor

    }

    constructor(id2: String?, title2: String?, timestamp2: String?, videoUri2: String?,  publisher: String,  postid: String) {
        this.id2 = id2
        this.title2 = title2
        this.timestamp2 = timestamp2
        this.videoUri2 = videoUri2
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