package com.kygoinc.edusphere.ui.resources

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kygoinc.edusphere.adapters.PdfResourceAdapter
import com.kygoinc.edusphere.databinding.FragmentDocumentResourcesBinding
import com.kygoinc.edusphere.models.ImageResource
import com.kygoinc.edusphere.models.PdfResource


class DocumentResourcesFragment : Fragment() {
    private lateinit var documentResourceAdapter: PdfResourceAdapter
    private lateinit var documentResourceRecyclerView: RecyclerView
    private lateinit var binding: FragmentDocumentResourcesBinding
    private var documentResourceList: ArrayList<PdfResource> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDocumentResourcesBinding.inflate(inflater, container, false)
//        Initialize recyclerview and its adapter
        documentResourceRecyclerView = binding.rcvDocumentRecyclerView
        documentResourceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        documentResourceAdapter = PdfResourceAdapter(requireContext(), documentResourceList)
        documentResourceRecyclerView.adapter = documentResourceAdapter

        // Fetch data from resources and add to imageResourceList
        fetchDocumentResources {
            Log.d("DocumentResourcesFragment", "DocumentResourceList: $documentResourceList")
            documentResourceAdapter.notifyDataSetChanged() // Notify adapter when data changes
        }


        return binding.root
    }

    private fun fetchDocumentResources(callback: (List<PdfResource>) -> Unit) {
        val databaseRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("/resources/documents")

        databaseRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                documentResourceList.clear()

                for (documentSnapshot in snapshot.children) {
                    val documentResource = documentSnapshot.getValue(PdfResource::class.java)
                    if (documentResource != null) {
                        documentResourceList.add(documentResource)
                    }
                }
                callback(documentResourceList)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.d("DocumentResourcesFragment", "Error: ${error.message}")
            }
        })

    }

}