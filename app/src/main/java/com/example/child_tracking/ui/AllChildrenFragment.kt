package com.example.child_tracking.ui

import SpaceItemDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.child_tracking.R
import com.example.child_tracking.data.Child
import com.example.child_tracking.data.UsersDataStore
import com.example.child_tracking.databinding.FragmentAllChildrenBinding
import com.example.child_tracking.ui.adapters.AllChildrenAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class AllChildrenFragment : Fragment() {

    private lateinit var binding: FragmentAllChildrenBinding
    private val viewModel: ChildTrackingViewModel by activityViewModels()
    private lateinit var parentEmail: String
    private lateinit var apdtr: AllChildrenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAllChildrenBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.getAllChildren(viewModel.email)}


    override fun onResume() {
                       super.onResume()
                       viewModel.listlivedata.observe(viewLifecycleOwner) {
                           apdtr.submitList(it)
                          // apdtr.notifyDataSetChanged()
                       }

                   }
    fun setupRecyclerView () {

        apdtr = AllChildrenAdapter( { child ->
           // val bundle = Bundle().apply { putSerializable("child", child) }
            findNavController().navigate(AllChildrenFragmentDirections.actionAllChildrenFragmentToChildOptionsFragment(child))
        })

        binding.recyclerView.apply {
            adapter = apdtr
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(SpaceItemDecoration(8))
                       }
                   }

    }




