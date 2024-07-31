package com.fatihden.roomdatabase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.fatihden.roomdatabase.databinding.FragmentListeBinding


class ListeFragment : Fragment() {
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}