package com.example.kotlininsta.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininsta.FeedActivity
import com.example.kotlininsta.Post
import com.example.kotlininsta.R
import com.example.kotlininsta.databinding.RecyclerRowBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.ArrayList
import kotlin.math.log

class recyclerAdapter(private val postList:ArrayList<Post>):RecyclerView.Adapter<recyclerAdapter.PostHolder>() {
    var pressCount:Int= 1
    var liked:Boolean=false

    class PostHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
    var counter:Int = 0

    override fun onBindViewHolder(holder: PostHolder, position: Int) {


        val postUUID = postList.get(position).uuid

        holder.binding.userNameTxt.text = postList.get(position).userName
        holder.binding.foodNameTxt.text = postList.get(position).foodName
        holder.binding.foodDescpTxt.text = postList.get(position).foodDescription
        holder.binding.timeStamp.text =  postList.get(position).date.toString()
        Picasso.get().load(postList.get(position).downloadURL).into(holder.binding.foodImage)

        if(Firebase.auth.currentUser!=null) {


            Firebase.firestore.collection("Posts").document(postUUID).collection("likedByThem")
                .document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener(){
                    if(it.get("condition").toString()=="liked"){
                        holder.binding.likeBtn.setImageResource(R.drawable.liketrue)
                    }
                    else{
                        holder.binding.likeBtn.setImageResource(R.drawable.like)
                    }
                }

            Firebase.firestore.collection("Posts").document(postUUID).get().addOnSuccessListener {
                var likecounter: Long? = it.getLong("likeCounter")
                holder.binding.likeCounter.text = likecounter.toString()

                holder.binding.likeBtn.setOnClickListener() {

                    println(postList.get(position).uuid)
                    if (likecounter != null) {

                        Firebase.firestore.collection("Posts").document(postUUID).collection("likedByThem")
                            .document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener(){

                                    if(it.get("condition").toString()=="liked"){
                                        likecounter--

                                        if (Firebase.auth.currentUser != null) {


                                            Firebase.firestore.collection("Posts")
                                                .document(postUUID)
                                                .update("likeCounter", likecounter)


                                        }

                                        Firebase.firestore.collection("Posts")
                                            .document(postUUID).collection("likedByThem").document(Firebase.auth.currentUser!!.uid).delete()

                                        holder.binding.likeBtn.setImageResource(R.drawable.like)

                                    }
                                else{

                                        likecounter++

                                        if (Firebase.auth.currentUser != null) {


                                            Firebase.firestore.collection("Posts")
                                                .document(postUUID)
                                                .update("likeCounter", likecounter)

                                            val likedBy = hashMapOf(
                                                "condition" to "liked",
                                                "userId" to Firebase.auth.currentUser!!.uid
                                            )

                                            Firebase.firestore.collection("Posts")
                                                .document(postUUID).collection("likedByThem").document(Firebase.auth.currentUser!!.uid)
                                                .set(likedBy).addOnSuccessListener(){

                                                }.addOnFailureListener {
                                                    Log.wtf("error",it.localizedMessage)
                                                }


                                        }
                                        holder.binding.likeBtn.setImageResource(R.drawable.liketrue)
                                    }

                            }.addOnFailureListener(){
                                Log.wtf("error",it.localizedMessage)
                            }}

                                    }
                                    }



                }
            }



        }










