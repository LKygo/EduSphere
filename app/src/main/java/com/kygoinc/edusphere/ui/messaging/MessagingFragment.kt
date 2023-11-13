package com.kygoinc.edusphere.ui.messaging

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.adapters.MessagingViewPagerAdapter
import com.kygoinc.edusphere.databinding.FragmentMessagingBinding
import com.kygoinc.edusphere.ui.login_signup.LoginActivity


class MessagingFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var _binding: FragmentMessagingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagingBinding.inflate(inflater, container, false)

        val toolbar = binding.myToolbar

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                return true
            }

            R.id.settings -> {
                Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(requireContext(), SettingsFragment::class.java)
//                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)

                return true
            }
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        val pagerAdapter =
            MessagingViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Chats"
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