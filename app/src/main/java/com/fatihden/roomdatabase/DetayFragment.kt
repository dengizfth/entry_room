package com.fatihden.roomdatabase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fatihden.roomdatabase.databinding.FragmentDetayBinding
import com.fatihden.roomdatabase.databinding.FragmentListeBinding


class DetayFragment : Fragment() {
    private var _binding: FragmentDetayBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.kaydetBtn.setOnClickListener {

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}