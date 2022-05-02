package com.spacenine.nemo.util

import android.location.Location
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*


var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
var databaseReference: DatabaseReference = firebaseDatabase.reference

fun updatePhoneLocation(phone: String, location: Location) {

    Thread {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.ref.child("nemo").child("phone-locations").child(phone).setValue(location)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }.start()

}

fun checkForPhoneNumber(phone: String, successListener: Runnable, failureListener: Runnable) {

    val phoneDatabaseReference = databaseReference.child("nemo").child("phone-locations").child(phone)

    val eventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists())
                successListener.run()
            else
                failureListener.run()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            failureListener.run()
        }
    }

    phoneDatabaseReference.addListenerForSingleValueEvent(eventListener)

}