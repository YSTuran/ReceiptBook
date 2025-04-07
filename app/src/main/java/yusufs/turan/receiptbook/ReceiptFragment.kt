package yusufs.turan.receiptbook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import yusufs.turan.receiptbook.databinding.FragmentListBinding
import yusufs.turan.receiptbook.databinding.FragmentReceiptBinding


class ReceiptFragment : Fragment() {

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener{selectImage(it)}
        binding.button.setOnClickListener { save(it) }
        binding.button2.setOnClickListener { delete(it) }

        arguments?.let {
            val infos= ReceiptFragmentArgs.fromBundle(it).info

            if (infos=="new"){
                //adding new receipt
                binding.button2.isEnabled=false
                binding.button.isEnabled=true
                binding.editText.setText("")
                binding.editText2.setText("")
            }
            else{
                //shows existing receipt
                binding.button2.isEnabled=true
                binding.button.isEnabled=false
            }
        }
    }

    fun save(view: View){

    }
    fun delete(view: View){

    }
    fun selectImage(view: View){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}