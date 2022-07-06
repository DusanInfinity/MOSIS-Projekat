package elfak.mosis.dsoftapartments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import elfak.mosis.dsoftapartments.data.User
import elfak.mosis.dsoftapartments.databinding.FragmentLoginBinding
import elfak.mosis.dsoftapartments.databinding.FragmentMapBinding
import elfak.mosis.dsoftapartments.model.UserViewModel

class MapFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!


    private val callback = object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nameText: TextView = binding.helloName
        val loadingProgressBar = binding.loading
        loadingProgressBar.visibility = View.VISIBLE

        userViewModel.getUserData()

        userViewModel.loginUser.observe(viewLifecycleOwner,
            Observer { user ->
                loadingProgressBar.visibility = View.GONE
                nameText.text = "${user?.name} ${user?.phoneNumber}"

            })
        val singOut : Button = requireView().findViewById(R.id.signout_button)
        singOut.setOnClickListener{
            auth.signOut()
            // logout ce da se drugacije odradi, ovo je cisto pokazno
            // treba da se obrise iz View modela logovani user
            // Ne znam kako jos uvek kad nadjem brisem
            userViewModel.logOut()
            findNavController().navigate(R.id.action_MapFragment_to_LogInFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStart() {
        super.onStart()
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onStop() {
        callback.remove()
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

}