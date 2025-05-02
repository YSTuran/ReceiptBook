package yusufs.turan.receiptbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import yusufs.turan.receiptbook.databinding.RecyclerRowBinding
import yusufs.turan.receiptbook.model.Receipt
import yusufs.turan.receiptbook.view.ListFragment
import yusufs.turan.receiptbook.view.ListFragmentDirections

class ReceiptAdapter(val myReceiptList: List<Receipt>) : RecyclerView.Adapter<ReceiptAdapter.ReceiptHolder>() {

    class ReceiptHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptHolder {
        val recyclerRowbinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReceiptHolder(recyclerRowbinding)
    }

    override fun getItemCount(): Int {
        return myReceiptList.size
    }

    override fun onBindViewHolder(holder: ReceiptHolder, position: Int) {
        holder.binding.recyclerViewText.text = myReceiptList[position].name
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToReceiptFragment(info = "isExist" , id = myReceiptList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}