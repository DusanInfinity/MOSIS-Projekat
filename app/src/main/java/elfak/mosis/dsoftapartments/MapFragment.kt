package elfak.mosis.dsoftapartments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.dsoftapartments.data.User
import elfak.mosis.dsoftapartments.model.UserViewModel

class MapFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val user = auth.currentUser
        if (user != null) {
            val nameText: TextView = requireView().findViewById(R.id.hello_name)
            nameText.text = userViewModel.currentUser.toString()
        }

        val singOut : Button = requireView().findViewById(R.id.signout_button)
        singOut.setOnClickListener{
            auth.signOut()
            Log.e("firebase", auth.currentUser.toString())
            userViewModel.currentUser = null
        }

        super.onViewCreated(view, savedInstanceState)
    }

}