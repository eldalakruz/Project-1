package com.example.myapplication.Model

class ModelAnswer {

    private var answer : String = ""
    private var publisher : String = ""

    var id : String? = null
    var title: String? = null
    var timestamp: String? = null
    var imageUri: String? = null


    var id1 : String? = null
    var title1: String? = null
    var timestamp1: String? = null
    var videoUri: String? = null
    //private var publisher : String = ""

    constructor(){

    }

    constructor(answer : String,publisher : String,id: String? , title: String?, timestamp: String?, imageUri: String?,id1: String? , title1: String?, timestamp1: String?, videoUri: String?){
        this.answer = answer
        this.publisher = publisher

        this.id = id
        this.title = title
        this.timestamp = timestamp
        this.imageUri = imageUri

        this.id1 = id1
        this.title1 = title1
        this.timestamp1 = timestamp1
        this.videoUri = videoUri
    }

    fun getanswer() : String{
        return answer
    }

    fun getPublisher() : String{
        return publisher
    }

    fun getimage(): String? {
        return imageUri
    }

    fun getvideo(): String? {
        return videoUri
    }
}