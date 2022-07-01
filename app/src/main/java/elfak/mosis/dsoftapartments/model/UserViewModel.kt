package elfak.mosis.dsoftapartments.model

import android.renderscript.Sampler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import elfak.mosis.dsoftapartments.data.User

class UserViewModel : ViewModel() {

    lateinit var database: DatabaseReference
    lateinit var storage: StorageReference
    var currentUser: User? = null


    fun setUserData(uid: String) {
        database = Firebase.database.getReference("Users")
        storage = FirebaseStorage.getInstance().getReference("Images/${uid}")
        database.child(uid).get().addOnCompleteListener {
            val result = it.result.getValue(User::class.java)
            currentUser = result
        }
    }

}