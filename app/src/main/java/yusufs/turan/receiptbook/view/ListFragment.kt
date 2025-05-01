package yusufs.turan.receiptbook.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import yusufs.turan.receiptbook.databinding.FragmentListBinding
import yusufs.turan.receiptbook.roomdb.ReceiptDAO
import yusufs.turan.receiptbook.roomdb.ReceiptDB


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: ReceiptDB
    private lateinit var receiptDAO: ReceiptDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext() , ReceiptDB::class.java , "Receipts").build()
        receiptDAO = db.receiptDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { newReceipt(it) }
    }

    fun newReceipt(view: View){
        val action=ListFragmentDirections.actionListFragmentToReceiptFragment(info = "new",id=0)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}