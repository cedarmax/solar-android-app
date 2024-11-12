package com.example.solar_bottom_view_navigation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.solar_bottom_view_navigation.databinding.FragmentDashboardBinding

import android.widget.ImageView
import android.widget.Switch
import com.example.solar_bottom_view_navigation.R

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding get() = _binding!!
    private lateinit var switch1: ImageView
    private lateinit var switch2: ImageView
    private lateinit var lightBulb1: ImageView
    private lateinit var lightBulb2: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
        */
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize the switches and light bulb images
        switch1 = view.findViewById(R.id.switch1)
        switch2 = view.findViewById(R.id.switch2)
        lightBulb1 = view.findViewById(R.id.lightBulb1)
        lightBulb2 = view.findViewById(R.id.lightBulb2)

        // Set up switch 1 listener
        switch1.setOnClickListener {
            if (switch1.drawable.constantState == resources.getDrawable(R.drawable.switch_off).constantState) {
                // Switch to ON state
                switch1.setImageResource(R.drawable.switch_on)
                lightBulb1.setImageResource(R.drawable.bulb_on)
            } else {
                // Switch to OFF state
                switch1.setImageResource(R.drawable.switch_off)
                lightBulb1.setImageResource(R.drawable.bulb_off)
            }
        }

        // Set up switch 2 listener
        switch2.setOnClickListener {
            if (switch2.drawable.constantState == resources.getDrawable(R.drawable.switch_off).constantState) {
                // Switch to ON state
                switch2.setImageResource(R.drawable.switch_on)
                lightBulb2.setImageResource(R.drawable.bulb_on)
            } else {
                // Switch to OFF state
                switch2.setImageResource(R.drawable.switch_off)
                lightBulb2.setImageResource(R.drawable.bulb_off)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}