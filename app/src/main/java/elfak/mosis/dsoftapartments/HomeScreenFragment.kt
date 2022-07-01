package elfak.mosis.dsoftapartments

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import elfak.mosis.dsoftapartments.databinding.HomeScreenFragmentBinding

class HomeScreenFragment : Fragment() {

    private var _binding: HomeScreenFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = HomeScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn : Button = requireView().findViewById(R.id.button_lets_start)
        btn.setOnClickListener {
            findNavController().navigate(R.id.action_HomeScreenFragment_to_LogInFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}