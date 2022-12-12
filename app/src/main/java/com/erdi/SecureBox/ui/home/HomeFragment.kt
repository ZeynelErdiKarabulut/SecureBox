package com.erdi.SecureBox.ui.home

import android.content.Context
import com.erdi.SecureBox.ui.home.HomeViewModel
import com.erdi.SecureBox.adapters.RecyclerViewAdapter
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.erdi.SecureBox.R
import android.widget.TextView
import android.view.WindowManager
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private val adapter: RecyclerViewAdapter? = null
    var PREF_NAME = "appEssentials"
    var PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE"
    var sharedPreferences: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView = root.findViewById<TextView>(R.id.text_home)
        homeViewModel!!.text.observe(viewLifecycleOwner) { s -> textView.text = s }
        sharedPreferences = this.requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (sharedPreferences!!.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        return root
    }
}