package com.kygoinc.edusphere.ui.resources

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.R
import com.kygoinc.edusphere.adapters.ImageResourceAdapter
import com.kygoinc.edusphere.adapters.VideoResourceAdapter
import com.kygoinc.edusphere.databinding.FragmentImageResourcesBinding
import com.kygoinc.edusphere.databinding.FragmentVideoResourcesBinding
import com.kygoinc.edusphere.models.ImageResource
import com.kygoinc.edusphere.models.VideoResource


class VideoResourcesFragment : Fragment() {

    private lateinit var videoResourceAdapter: VideoResourceAdapter
    private lateinit var videoResourceRecyclerView: RecyclerView
    private lateinit var binding: FragmentVideoResourcesBinding
    private var videoResourceList: ArrayList<VideoResource> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoResourcesBinding.inflate(inflater, container, false)
        val view = binding.root


        // Initialize RecyclerView and its adapter
        videoResourceRecyclerView = binding.rcvViewVideoResources
        videoResourceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        videoResourceAdapter = VideoResourceAdapter(requireContext(), videoResourceList)
        videoResourceRecyclerView.adapter = videoResourceAdapter

        // Fetch data from resources and add to imageResourceList
        fetchVideoResources {
            Log.d("ImageResourcesFragment", "ImageResourceList: $videoResourceList")
            videoResourceAdapter.notifyDataSetChanged() // Notify adapter when data changes
        }

        return view
    }

    private fun fetchVideoResources(callback: (List<VideoResource>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/resources/videos")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                videoResourceList.clear()

                for (dataSnapshot in snapshot.children) {
                    val imageResource = dataSnapshot.getValue(VideoResource::class.java)
                    imageResource?.let {
                        videoResourceList.add(it)
//                        Reverse order of the list
                        videoResourceList.reverse()
                    }

                }
                callback.invoke(videoResourceList)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback.invoke(emptyList())
            }
        })
    }


}