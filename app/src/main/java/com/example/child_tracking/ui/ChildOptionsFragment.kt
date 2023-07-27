package com.example.child_tracking.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.child_tracking.data.Child
import com.example.child_tracking.databinding.FragmentAllChildrenBinding
import com.example.child_tracking.databinding.FragmentChildOptionsBinding



class ChildOptionsFragment : Fragment() {

    private lateinit var binding: FragmentChildOptionsBinding
    private val viewModel: ChildTrackingViewModel by activityViewModels()
   // val args: ChildOptionsFragmentArgs by navArgs()

    private lateinit var child: Child


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       child = arguments.let {
            it?.getSerializable("child", Child::class.java)
        }  as Child

        viewModel.setCurrentChild(child)
        binding = FragmentChildOptionsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.titleTextView.text = child.childName
        binding.descriptionTextView.text = child.locationLastTimeUpdated

        binding.showLocationButton.setOnClickListener{
            viewModel.startListeningChanges()
            findNavController().navigate(ChildOptionsFragmentDirections.actionChildOptionsFragmentToMapsFragment(child))
        }

        binding.sendRecordSignal.apply {
            setOnClickListener{
                viewModel.sendRecordSignal()
                viewModel.startListeningChanges()
                visibility = View.INVISIBLE
               findNavController().navigate(ChildOptionsFragmentDirections.actionChildOptionsFragmentToMediaPlayerFragment())
            }
        }


        super.onViewCreated(view, savedInstanceState)




}

    override fun onResume() {


        super.onResume()
    }


}