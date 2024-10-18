package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobdeve.s13.estanol.miguelfrancis.mp.databinding.FragmentStatusBinding

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statusViewModel = ViewModelProvider(this).get(StatusViewModel::class.java)

        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textStatus
        statusViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}