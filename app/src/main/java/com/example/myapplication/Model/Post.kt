package com.example.myapplication.Model


class Post {


    var videoUri: String = ""
    private var postid : String = ""
    private var postimage : String = ""
    private var publisher : String = ""
    private var description : String = ""

    private var pollquestion : String = ""
    private var contestantone : String = ""
    private var contestanttwo : String = ""




    constructor(
        postid: String,
        postimage: String,
        publisher: String,
        description: String,
        pollquestion: String,
        contestantone: String,
        contestanttwo: String
    )

    {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
        this.pollquestion = pollquestion
        this.contestantone = contestantone
        this.contestanttwo = contestanttwo

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

    fun getPollquestion() : String{
        return pollquestion
    }

    fun getContestantone() : String{
        return contestantone
    }

    fun getContestanttwo() : String{
        return contestanttwo
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

    fun setPollquestion(pollquestion: String){
        this.pollquestion = pollquestion
    }

    fun setContestantone(contestantone: String){
        this.contestantone = contestantone
    }

    fun setContestanttwo(contestanttwo: String){
        this.contestanttwo = contestanttwo
    }
}