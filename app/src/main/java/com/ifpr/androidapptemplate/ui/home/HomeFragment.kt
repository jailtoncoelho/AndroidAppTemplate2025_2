package com.ifpr.androidapptemplate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ifpr.androidapptemplate.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate do layout base.
        // Lembre-se que o destino de listagem na sua navegação é o CharacterListFragment.
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    // O onDestroyView pode ser removido se o Fragment não tiver um binding
}