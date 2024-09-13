package com.example.kotlininsta

import com.google.firebase.Timestamp
import java.util.Date

data class Post(val foodName:String, val userName:String, val foodDescription:String, val downloadURL:String, val date: String, val likeCounter:Int,val uuid:String) {

}