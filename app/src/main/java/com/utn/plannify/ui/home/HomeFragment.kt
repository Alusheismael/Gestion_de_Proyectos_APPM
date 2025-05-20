package com.utn.plannify.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.utn.plannify.R
import com.utn.plannify.databinding.FragmentHomeBinding
import com.utn.plannify.ui.ProyectoActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGestion = view.findViewById<LinearLayout>(R.id.btnGestionProyectos)
        btnGestion.setOnClickListener {
            val intent = Intent(requireContext(), ProyectoActivity::class.java)
            intent.putExtra("usuarioId", requireActivity().intent.getIntExtra("usuarioId", -1))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
