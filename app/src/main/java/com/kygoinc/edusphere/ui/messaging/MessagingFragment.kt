package com.kygoinc.edusphere.ui.messaging

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.adapters.MessagingViewPagerAdapter
import com.kygoinc.edusphere.databinding.FragmentMessagingBinding
import com.kygoinc.edusphere.models.GroupI
import com.kygoinc.edusphere.models.GroupInfo
import com.kygoinc.edusphere.ui.login_signup.LoginActivity


class MessagingFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var _binding: FragmentMessagingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

            R.id.createGroup -> {
                showCreateGroupDialog()
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

    private fun showCreateGroupDialog() {

        // Create dialog to get group name
        val dialog =
            AlertDialog.Builder(requireContext(), R.style.AlertDialog).setTitle("Create Group")

        val groupName = EditText(requireContext())
        groupName.hint = "Group Name"

        dialog.setView(groupName)
        dialog.setMessage("Enter group name").setPositiveButton("Create",
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                val groupN = groupName.text.toString()
                val groupA = ""
                if (groupN.isEmpty()) {
                    Toast.makeText(
                        requireContext(), "Please enter a group name", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    createNewGroup(groupN, groupA)
                }
            }).setNegativeButton("Cancel",
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel()
            })

        dialog.show()

    }

    private fun createNewGroup(groupN: String, groupAbout: String) {
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Reference to the "groups" node in Firebase Realtime Database
        val groupRef = FirebaseDatabase.getInstance().getReference("Groups")

        // Create a unique key for the new group

        val newGroupRef = groupRef.push()

// Clean key path and remove invalid characters
        val sanitizedKey = newGroupRef.key?.replace("[.#$]".toRegex(), "")
        val groupInfo = GroupI(sanitizedKey!!, groupN, groupAbout)

        // Set the value of the new group in the database
        newGroupRef.setValue(groupInfo)

        // Create a list of members (initially only the user who created the group)

        val members = mutableListOf<String>()
        members.add(userId!!)

        // Set the list of members for the new group in the database

        FirebaseDatabase.getInstance().getReference("Groups/$sanitizedKey/members")
            .setValue(members).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "$groupN has been successfully created",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Error creating group", Toast.LENGTH_SHORT)
                        .show()
                }
            }
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


