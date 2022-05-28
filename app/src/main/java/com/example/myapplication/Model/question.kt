package com.example.myapplication.Model

class question {

    var id : String = ""
    var Question : String = ""
    private var publisher : String = ""
    var timestamp: String = ""
    var imageUri : String? = null


    constructor()

    constructor(id:String , question:String, publisher: String,timestamp: String,imageUri: String){

        this.id=id
        this.Question=question
        this.publisher=publisher
        this.timestamp = timestamp
        this.imageUri = imageUri

    }

   fun getPublisher() : String {
      return publisher
    }

//
//    fun getid() : String? {
//        return id
//  }
//
//    fun getquestion() : String? {
//        return Question
//    }


}