package com.fatihden.roomdatabase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.core.graphics.decodeBitmap
import com.fatihden.roomdatabase.databinding.FragmentDetayBinding
import com.fatihden.roomdatabase.databinding.FragmentListeBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


class DetayFragment : Fragment() {
    private var _binding: FragmentDetayBinding? = null
    private val binding get() = _binding!!
    // izin - permission
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    private var secilenGorsel : Uri? = null  // data/user/media/...
    private var secilenBitmap : Bitmap? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetayBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation argüman bilgi çekme :
        arguments?.let {
            val bilgi = DetayFragmentArgs.fromBundle(it).bilgi

            if (bilgi == "yeni"){
                // Yeni tarif eklenecek :
                binding.silBtn.isEnabled= false
                binding.kaydetBtn.isEnabled =  true
                binding.nameET.setText("")
                binding.detailET.setText("")
            } else {
                // Eski eklenmiş gösteriliyor
                binding.silBtn.isEnabled = true
                binding.kaydetBtn.isEnabled =  false
            }
        }
        // Kaydet :
        binding.kaydetBtn.setOnClickListener {

        }
        binding.silBtn.setOnClickListener {

        }
        // Resim Seçimi :
        binding.imageView.setOnClickListener {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED ) {
                    // izin verilmiş , Galeriye Git
                    if(ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),Manifest.permission.READ_MEDIA_IMAGES
                        )){
                        // izin istememiz lazım ,
                        // Kullanıcıdan neden izin istediğimizi bir kez daha söyleyerek izin istememiz lazım
                        Snackbar.make(it,"Galeriye ulaşıp görsel seçmemiz lazım!!" ,
                            Snackbar.LENGTH_INDEFINITE).setAction(
                            "İzin Ver " ,
                            {
                                // İzin İsteyeceğiz :
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        ).show()
                    } else {
                        // izin İsteyeceğiz :
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    // izin verilmemiş , Tekra izin için sor
                    val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }

            } else {
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ) {
                    // izin verilmiş , Galeriye Git
                    if(ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE
                        )){
                        // izin istememiz lazım ,
                        // Kullanıcıdan neden izin istediğimizi bir kez daha söyleyerek izin istememiz lazım
                        Snackbar.make(it,"Galeriye ulaşıp görsel seçmemiz lazım!!" ,
                            Snackbar.LENGTH_INDEFINITE).setAction(
                            "İzin Ver " ,
                            {
                                // İzin İsteyeceğiz :
                                permissionLauncher.launch(
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                )


                            }
                        ).show()
                    } else {
                        // izin İsteyeceğiz :
                        permissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                } else {
                    // izin verilmemiş , Tekra izin için sor
                    val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            }

            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
                // izin verilmiş , Galeriye Git
                if(ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE
                )){
                    // izin istememiz lazım ,
                    // Kullanıcıdan neden izin istediğimizi bir kez daha söyleyerek izin istememiz lazım
                    Snackbar.make(it,"Galeriye ulaşıp görsel seçmemiz lazım!!" ,
                            Snackbar.LENGTH_INDEFINITE).setAction(
                                "İzin Ver " ,
                                    {
                                        // İzin İsteyeceğiz :
                                        permissionLauncher.launch(
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        )


                                    }
                            ).show()
                } else {
                    // izin İsteyeceğiz :
                    permissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            } else {
                // izin verilmemiş , Tekra izin için sor
                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }
        }

    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if ( result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if(intentFromResult != null ){
                    // içersi dolu ise :

                    secilenGorsel = intentFromResult.data
                    try {
                        if ( Build.VERSION.SDK_INT >= 28 ) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,secilenGorsel!!
                            )
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        } else {

                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    } catch ( e0 : IOException) {
                        println( e0.localizedMessage)
                    } catch (e1 : Exception) {
                        println(e1.message)
                    }



                }
            }

        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        {result ->
            if (result) {
                // izin verildi
                // galeriye gidebiliriz
                val intentToGallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            } else {
                // izin verilmedi
                Toast.makeText(requireContext(),"İzin Verilmedi",Toast.LENGTH_LONG).show()

            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}