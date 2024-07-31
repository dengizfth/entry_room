package com.fatihden.roomdatabase.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.fatihden.roomdatabase.databinding.FragmentListeBinding
import com.fatihden.roomdatabase.db.DetailDAO
import com.fatihden.roomdatabase.db.DetailDatabase
import com.fatihden.roomdatabase.model.Detail
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListeFragment : Fragment() {
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!

    // database : ( declaration )
    private lateinit var db : DetailDatabase
    private lateinit var detayDao : DetailDAO

    private val mDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // database ( initialize )
        db = Room.databaseBuilder(
            requireContext(),
            DetailDatabase::class.java ,
            "Detaylar"
        ).build()
        detayDao = db.detailDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // + ekleme Floatin Action Button
        binding.listeFAB.setOnClickListener{
            val action = ListeFragmentDirections.actionListeFragmentToDetayFragment("yeni",id=0)
            Navigation.findNavController(it).navigate(action)
        }
        binding.listeRW.layoutManager = LinearLayoutManager( requireContext() )
        verileriAl()

    }
    private fun verileriAl(){
        mDisposable.add(
            detayDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }
    private fun handleResponse(detaylar : List<Detail>) {
        detaylar.forEach {
            println(it.isim)
            println(it.detay)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

        mDisposable.clear()
    }


}