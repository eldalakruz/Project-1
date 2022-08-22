package com.example.myapplication.Model

class AllPost {

    private var postid : String = ""
    private var publisher : String = ""
    private var description : String = ""

    private var pollquestion : String = ""
    private var contestantone : String = ""
    private var contestanttwo : String = ""

    private var postimage : String = ""
    var videoUri: String = ""


    constructor()

    constructor(
                videoUri: String,
                postimage: String,
                postid: String,
                publisher: String,
                description: String,
                pollquestion: String,
                contestantone: String,
                contestanttwo: String )

    {

        this.videoUri = videoUri
        this.postimage = postimage
        this.postid = postid
        this.publisher = publisher
        this.description = description
        this.pollquestion = pollquestion
        this.contestantone = contestantone
        this.contestanttwo = contestanttwo
    }


    @JvmName("getVideoUri1")
    fun getVideoUri() : String{
    return videoUri
    }

    fun getPostimage() : String{
        return  postimage
    }


    fun getPostid() : String{
        return  postid
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


    fun setPostimage(postimage: String){
        this.postimage = postimage
    }

    fun setPostid(postid: String){
        this.postid = postid
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