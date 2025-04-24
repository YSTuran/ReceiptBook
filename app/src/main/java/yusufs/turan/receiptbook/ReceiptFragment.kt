package yusufs.turan.receiptbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import yusufs.turan.receiptbook.databinding.FragmentReceiptBinding
import java.io.IOException


class ReceiptFragment : Fragment() {

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedImage : Uri?=null
    private var selectedBitmap : Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerlauncher()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}