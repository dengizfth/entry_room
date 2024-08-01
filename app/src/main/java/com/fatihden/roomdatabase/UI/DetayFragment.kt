package com.fatihden.roomdatabase.UI

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
import com.fatihden.roomdatabase.databinding.FragmentDetayBinding
import com.fatihden.roomdatabase.db.DetailDAO
import com.fatihden.roomdatabase.db.DetailDatabase
import com.fatihden.roomdatabase.model.Detail
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException


class DetayFragment : Fragment() {
    private var _binding: FragmentDetayBinding? = null
    private val binding get() = _binding!!
    // izin - permission
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    private var secilenGorsel : Uri? = null  // data/user/media/...
    private var secilenBitmap : Bitmap? = null
    //mDisposale : kullan at . binlerce db'ye istek atılıp ram'de yer kaplar ve bu yer kaplamaları
    // ve istekleri karışılarken uygulama kapatılınca Ram'e birikenleri çöpe atabiliriz ( onDestory'de için boşaltılır)
    private val mDisposable = CompositeDisposable()

    // database : ( declaration )
    private lateinit var db : DetailDatabase
    private lateinit var detayDao : DetailDAO
    // listede seçilen :
    private var selectedDetail : Detail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLauncher()

        // database ( initialize )
        db = Room.databaseBuilder(
            requireContext(),
            DetailDatabase::class.java ,
            "Detaylar"
        )
            .build()
            //java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
            // hatasını Almamak için  . !!! Ama MainThread 'e yüklenmiş olucağımız için burada kullanmak doğru değil
            // Bunun yerine Thread işlemi ile gerçekleştir ki uygulamaya aşırı yüklenmesin.
            //.allowMainThreadQueries()

        detayDao = db.detailDao()

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
                selectedDetail = null
            } else {
                // Eski eklenmiş gösteriliyor
                binding.silBtn.isEnabled = true
                binding.kaydetBtn.isEnabled =  false
                val id = DetayFragmentArgs.fromBundle(it).id

                mDisposable.add(
                    detayDao.findById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )

            }
        }

        // Kaydet :
        binding.kaydetBtn.setOnClickListener {
            val nameTxt = binding.nameET.text.toString()
            val detayTxt = binding.detailET.text.toString()

            if( secilenBitmap != null) {
                val kucukBitmap = kucukBitmapOlustur(secilenBitmap!! , 300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG , 50 ,outputStream)
                val byteDizisi = outputStream.toByteArray()

                val detayInsert = Detail(nameTxt , detayTxt , byteDizisi)

                // RxJava :
                // Db'ye insert işlemi yapıldı.
                //mDisposable ile ram'de birikenleri sonradan temizleme şansımız olucak
                mDisposable.add(
                    detayDao.insert(detayInsert)
                        .subscribeOn(Schedulers.io()) // hem db hemde interne işlemler
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseForInsert) // Sonucu aldıktan sonra içersindeki func'unu tetikler
                )

            }


        }

        // Sil :
        binding.silBtn.setOnClickListener {

            if(selectedDetail != null){
                mDisposable.add(
                    detayDao.delete(detay = selectedDetail!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this:: handleResponseForInsert)
                )
            }

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
    // RecyclerView Liste üzerinden tıklanan değerin id'sine göre
    // verileri Detay Fragment'te gösteren func
    private fun handleResponse(detay : Detail){
        binding.nameET.setText(detay.isim)
        binding.detailET.setText(detay.detay)
        val bitmap = BitmapFactory.decodeByteArray(detay.gorsel, 0,detay.gorsel.size)
        binding.imageView.setImageBitmap(bitmap)
        selectedDetail = detay
    }



    // db işleminden sonra tetiklenilecek function ,
    // Tetiklenince ListeFragment'e gider
    private fun handleResponseForInsert() {
        // bir önceki fragmmente döndür
        val action = DetayFragmentDirections.actionDetayFragmentToListeFragment()
        Navigation.findNavController(requireView()).navigate(action)

    }
    private fun kucukBitmapOlustur(
        kullaniciinSectigiBitmap: Bitmap ,
        maximum :Int
    ) : Bitmap {

        var width = kullaniciinSectigiBitmap.width
        var heigh = kullaniciinSectigiBitmap.height

        val bitmapOrani : Double = width.toDouble() / heigh.toDouble()

        if ( bitmapOrani > 1){
            // görsel yatay
            width = maximum
            val kisaltilmisYukseklik = width / bitmapOrani
            heigh = kisaltilmisYukseklik.toInt()
        } else {
            // görsel Dikey
            heigh = maximum
            val kisaltilmisGenislik = heigh * bitmapOrani
            width = kisaltilmisGenislik.toInt()
        }

        return Bitmap.createScaledBitmap(kullaniciinSectigiBitmap, width, heigh, false)
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

        mDisposable.clear() // Ram'deki verileri temizledi
    }
}