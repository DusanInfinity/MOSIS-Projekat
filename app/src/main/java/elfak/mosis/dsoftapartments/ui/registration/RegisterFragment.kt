package elfak.mosis.dsoftapartments.ui.registration

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.dsoftapartments.R
import elfak.mosis.dsoftapartments.data.User
import elfak.mosis.dsoftapartments.databinding.FragmentRegisterBinding
import java.io.ByteArrayOutputStream


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_CODE = 101
    private var imageTaken: Bitmap? = null

    private val auth = FirebaseAuth.getInstance()
    lateinit var database: DatabaseReference

    private var storage = FirebaseStorage.getInstance()

    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val regBtn: Button = binding.registerButtonRegistration
        val loginButton: Button = binding.registerButtonLogin
        val cameraBtn: ImageButton = binding.imageButtonAdd


        loginButton.setOnClickListener {
            findNavController().popBackStack()
        }

        regBtn.setOnClickListener(View.OnClickListener {
            register()
        })

        cameraBtn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }
        }

        setOnClickListenersForAllEditTexts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage: Bitmap = data?.extras?.get("data") as Bitmap
            val matrix = Matrix()
            matrix.postRotate(90.0F)
            val rotatedImage = Bitmap.createBitmap(
                takenImage,
                0,
                0,
                takenImage.width,
                takenImage.height,
                matrix,
                true
            )
            imageTaken = rotatedImage
            val imageView: ImageView = binding.registrationImageView
            imageView.setImageBitmap(rotatedImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun register() {
        val nameEditText: EditText = binding.registerName
        val emailEditText: EditText = binding.registerEmail
        val phoneEditText: EditText = binding.registerPhone
        val passwordEditText: EditText = binding.registerPassword
        val loadingProgressBar = binding.loading
        var calledRegister = false


        if (nameEditText.text.isEmpty()) {
            binding.registerNameTextLayout.error = "Name is required"
            nameEditText.requestFocus()
            return
        }
        if (emailEditText.text.isEmpty()) {
            binding.registerEmailTextLayout.error = "Email address is required"
            emailEditText.requestFocus()
            return
        }
        if (!registerViewModel.isEmailValid(emailEditText.text.toString())) {
            binding.registerEmailTextLayout.error = "Email address has incorrect form"
            emailEditText.requestFocus()
            return
        }

        if (phoneEditText.text.isEmpty()) {
            binding.registerPhoneTextLayout.error = "Phone number is required"
            phoneEditText.requestFocus()
            return
        }

        if (!registerViewModel.isPhoneValid(phoneEditText.text.toString())) {
            binding.registerPhoneTextLayout.error = "Phone number is incorrect"
            phoneEditText.requestFocus()
            return
        }

        if (passwordEditText.text.isEmpty()) {
            binding.registerPasswordTextLayout.error = "Password is required"
            passwordEditText.requestFocus()
            return
        }

        if (!registerViewModel.isPasswordValid(passwordEditText.text.toString())) {
            binding.registerPasswordTextLayout.error = "Password must be at least 8 character long"
            passwordEditText.requestFocus()
            return
        }

        loadingProgressBar.visibility = View.VISIBLE
        registerViewModel.register(
            User(
                "",
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                phoneEditText.text.toString(),
                "",
                imageTaken
            ), passwordEditText.text.toString()
        )
        calledRegister = true

        registerViewModel.registerResult.observe(viewLifecycleOwner,
            Observer { result ->
                if(calledRegister){
                    loadingProgressBar.visibility = View.GONE
                    result.success.let {
                        Toast.makeText(view?.context, it, Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_RegisterFragment_to_MapFragment)
                    }
                    result.error.let{
                        Toast.makeText(view?.context, it, Toast.LENGTH_LONG).show()
                    }
                    calledRegister=false
                }
            })

    }

    private fun setOnClickListenersForAllEditTexts(){
        val nameEditText: EditText = binding.registerName
        val emailEditText: EditText = binding.registerEmail
        val phoneEditText: EditText = binding.registerPhone
        val passwordEditText: EditText = binding.registerPassword

        nameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.registerNameTextLayout.error = ""
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.registerEmailTextLayout.error = ""
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        phoneEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.registerPhoneTextLayout.error = ""
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.registerPasswordTextLayout.error = ""
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }


    private fun uploadImage() {
        val storageRef = storage.getReference("Images/${auth.currentUser?.uid}")
        val baos = ByteArrayOutputStream()
        imageTaken?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        storageRef.putBytes(data)
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

}