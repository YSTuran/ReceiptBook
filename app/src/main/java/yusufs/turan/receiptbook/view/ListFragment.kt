package yusufs.turan.receiptbook.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import yusufs.turan.receiptbook.adapter.ReceiptAdapter
import yusufs.turan.receiptbook.databinding.FragmentListBinding
import yusufs.turan.receiptbook.model.Receipt
import yusufs.turan.receiptbook.roomdb.ReceiptDAO
import yusufs.turan.receiptbook.roomdb.ReceiptDB


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: ReceiptDB
    private lateinit var receiptDAO: ReceiptDAO
    private val mDisposable = CompositeDisposable()

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
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        getDatas()
    }

    private fun getDatas(){
        mDisposable.add(receiptDAO.getAll().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponses))
    }

    private fun handleResponses(receipts: List<Receipt>){
        val adapter = ReceiptAdapter(receipts)
        binding.recyclerView.adapter = adapter
    }

    fun newReceipt(view: View){
        val action=ListFragmentDirections.actionListFragmentToReceiptFragment(info = "new", id = -1)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}