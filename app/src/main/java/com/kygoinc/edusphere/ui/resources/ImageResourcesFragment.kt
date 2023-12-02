package com.kygoinc.edusphere.ui.resources

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kygoinc.edusphere.adapters.ImageResourceAdapter
import com.kygoinc.edusphere.databinding.FragmentImageResourcesBinding
import com.kygoinc.edusphere.models.ImageResource


class ImageResourcesFragment : Fragment() {

    private lateinit var imageResourceAdapter: ImageResourceAdapter
    private lateinit var imageResourceRecyclerView: RecyclerView
    private lateinit var binding: FragmentImageResourcesBinding
    private var imageResourceList: ArrayList<ImageResource> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//       Inflate layout with view binding
        binding = FragmentImageResourcesBinding.inflate(inflater, container, false)
        val view = binding.root


        // Initialize RecyclerView and its adapter
        imageResourceRecyclerView = binding.rcvViewImageResources
        imageResourceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imageResourceAdapter = ImageResourceAdapter(requireContext(), imageResourceList)
        imageResourceRecyclerView.adapter = imageResourceAdapter

        // Fetch data from resources and add to imageResourceList
        fetchImageResources {
            Log.d("ImageResourcesFragment", "ImageResourceList: $imageResourceList")
            imageResourceAdapter.notifyDataSetChanged() // Notify adapter when data changes
        }

        return view
    }

    //    Fetch data from resources and add to imageResourceList
    private fun fetchImageResources(callback: (List<ImageResource>) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/resources/images")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageResourceList.clear()

                for (dataSnapshot in snapshot.children) {
                    val imageResource = dataSnapshot.getValue(ImageResource::class.java)
                    imageResource?.let {
                        imageResourceList.add(it)
//                        Reverse order of the list
                        imageResourceList.reverse()
                    }

                }

                callback.invoke(imageResourceList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback.invoke(emptyList())
            }
        })
    }


}
