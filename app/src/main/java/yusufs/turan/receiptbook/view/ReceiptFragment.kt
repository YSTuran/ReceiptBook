package yusufs.turan.receiptbook.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import yusufs.turan.receiptbook.databinding.FragmentReceiptBinding
import yusufs.turan.receiptbook.model.Receipt
import yusufs.turan.receiptbook.roomdb.ReceiptDAO
import yusufs.turan.receiptbook.roomdb.ReceiptDB
import java.io.ByteArrayOutputStream
import java.io.IOException


class ReceiptFragment : Fragment() {

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedImage : Uri?=null
    private var selectedBitmap : Bitmap?=null
    private val mDisposable = CompositeDisposable()

    private lateinit var db: ReceiptDB
    private lateinit var receiptDAO: ReceiptDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerlauncher()

        db = Room.databaseBuilder(requireContext() , ReceiptDB::class.java , "Receipts").build()
        receiptDAO = db.receiptDao()
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
                val id = ReceiptFragmentArgs.fromBundle(it).id
                mDisposable.add(
                    receiptDAO.findById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }
        }
    }

    private fun handleResponse(receipt: Receipt){
        binding.editText.setText(receipt.name)
        binding.editText2.setText(receipt.ingredients)
        val bitmap = BitmapFactory.decodeByteArray(receipt.pic, 0 , receipt.pic.size)
        binding.imageView.setImageBitmap(bitmap)
    }

    fun save(view: View){
        val name = binding.editText.text.toString()
        val ingredients= binding.editText2.text.toString()

        if(selectedBitmap != null)
        {
            val smallBitmap = createSmallBitmap(selectedBitmap!! , 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG , 50 ,outputStream)
            val myByteArray = outputStream.toByteArray()

            val myReceipt = Receipt(name , ingredients , myByteArray)

            mDisposable.add(receiptDAO.insert(myReceipt).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForInsert))

        }
    }

    private fun handleResponseForInsert(){
        val action = ReceiptFragmentDirections.actionReceiptFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    fun delete(view: View) {

    }
    fun selectImage(view: View){
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
      {
          if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
          {
              if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                  Snackbar.make(view,"You need to access the gallery and select an image.", Snackbar.LENGTH_INDEFINITE).setAction(
                      "Allow", View.OnClickListener { permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES) }
                  ).show()
              }
              else{
                  permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
              }
          }
          else{
              val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
              activityResultLauncher.launch(intentToGallery)
          }
      }
      else
      {
          if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
          {
              if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                  Snackbar.make(view,"You need to access the gallery and select an image.", Snackbar.LENGTH_INDEFINITE).setAction(
                      "Allow", View.OnClickListener { permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                  ).show()
              }
              else{
                  permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
              }
          }
          else{
              val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
              activityResultLauncher.launch(intentToGallery)
          }
      }
    }

    fun registerlauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
           if (result.resultCode == AppCompatActivity.RESULT_OK){
              val intentFromResult = result.data
               if(intentFromResult != null){
                   selectedImage = intentFromResult.data

                try
                {
                   if(Build.VERSION.SDK_INT >= 28)
                   {
                       val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedImage!!)
                       selectedBitmap = ImageDecoder.decodeBitmap(source)
                       binding.imageView.setImageBitmap(selectedBitmap)
                   }
                   else
                   {
                       selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImage)
                       binding.imageView.setImageBitmap(selectedBitmap)
                   }

                }
                catch (e: IOException)
                {
                   println(e.localizedMessage)
                }
               }

           }
        }

       permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
           if(result){
               val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentToGallery)
           }
           else{
               Toast.makeText(requireContext(),"Permission Denied!", Toast.LENGTH_LONG).show()
           }
       }
    }

    private fun createSmallBitmap(userSelection : Bitmap , maxValue : Int) : Bitmap{
        var width = userSelection.width
        var height = userSelection.height

        var bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1 ){
            //image horizontal
            width = maxValue
            val shortenedHeight = width / bitmapRatio
            height = shortenedHeight.toInt()
        }
        else{
            //image vertical
            height = maxValue
            val shortenedWidth = height * bitmapRatio
            width = shortenedWidth.toInt()
        }


        return Bitmap.createScaledBitmap(userSelection , width , height , true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}