package com.kygoinc.edusphere.ui.messaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kygoinc.edusphere.adapters.MessagingViewPagerAdapter
import com.kygoinc.edusphere.databinding.FragmentMessagingBinding

class MessagingFragment : Fragment() {

    private lateinit var  viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var _binding: FragmentMessagingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentMessagingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout=binding.tabLayout
        viewPager =binding.viewPager
        val pagerAdapter = MessagingViewPagerAdapter(childFragmentManager,viewLifecycleOwner.lifecycle)
        viewPager.adapter=pagerAdapter

        TabLayoutMediator(tabLayout,viewPager){tab,position->
            tab.text= when(position){
                0 ->"Chats"
                1 -> "Communities"
                2 -> "Friends"
                else -> null
            }

        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}