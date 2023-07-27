package com.example.child_tracking.ui

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.child_tracking.R
import com.example.child_tracking.databinding.FragmentChildOptionsBinding
import com.example.child_tracking.databinding.FragmentMediaPlayerBinding

class MediaPlayerFragment : Fragment() {


    private lateinit var binding: FragmentMediaPlayerBinding
    private val viewModel: ChildTrackingViewModel by activityViewModels()
    private var player: MediaPlayer? = null
    private var ref: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMediaPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        player = MediaPlayer()

        super.onViewCreated(view, savedInstanceState)

        binding.playButton.setOnClickListener() {
            play()
        }

    }
    override fun onResume() {
        viewModel.childLiveData.observe(viewLifecycleOwner) { child ->
            if (child.doneWithUploading) {
                binding.progressIndicator.visibility = View.INVISIBLE
                binding.playButton.visibility = View.VISIBLE
                ref = child.referenceToAudioRecord
            }
        }
        super.onResume()
    }

    fun play () {
        player!!.apply {
            try {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(ref!!)

            prepare()
            binding.playButton.visibility = View.INVISIBLE

            start()
                binding.pauseButton.visibility = View.VISIBLE


                setOnCompletionListener {
                    binding.pauseButton.visibility = View.GONE
                    binding.playButton.visibility = View.VISIBLE
                }
        } catch (e: Exception) {
        e.printStackTrace()}
        }
    }

    override fun onStop() {
        player?.release()
        super.onStop()
    }
}