package elfak.mosis.dsoftapartments.model

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.renderscript.Sampler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import elfak.mosis.dsoftapartments.data.User
import kotlin.math.log

class UserViewModel : ViewModel() {

    private var loggedUser: FirebaseUser? = Firebase.auth.currentUser
    private val database = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.getReference("UserProfileImages")

    private val _loginUser = MutableLiveData<User?>()
    var loginUser: LiveData<User?> = _loginUser

    fun getUserData() {

        if(loggedUser != null){
            database.collection("Users").document(loggedUser!!.uid).get()
                .addOnSuccessListener{
                    val userData = it.toObject<User>()
                    if (userData != null){
//                        val imageRef = storageRef.child(loggedUser.uid)
                        _loginUser.value = userData!!
                    }
                }
        }
    }

    fun logOut(){
        _loginUser.value = null

        // mora i loggedUser da se stavi na null jer za slucaj da se
        // vrati na neki fragment posle logout (Sto ne bi trebalo da se desi svakako)
        // da mu se vrati null i svakako nece da dobije nikakve vrednosti fragment
        // OVO SE NIKAD NE BI TREBALO DESITI ALI PREVENTIVNO NEKA BUDE
        // ako vracam na staro samo vrati loggedUser na val i nece trebati da ima dodatne provere sa !!
        loggedUser = null
    }
}