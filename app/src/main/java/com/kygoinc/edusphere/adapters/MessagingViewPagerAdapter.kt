package com.kygoinc.edusphere.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kygoinc.edusphere.ui.ChatsFragment
import com.kygoinc.edusphere.ui.CommunitiesFragment
import com.kygoinc.edusphere.ui.FriendsFragment

class MessagingViewPagerAdapter
    (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

        private val fragments = mutableListOf<Fragment>(
            ChatsFragment(),
            CommunitiesFragment(),
            FriendsFragment()
        )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment = fragments[position]
}
