package com.example.kotlintask3.ui


import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.kotlintask3.MainActivity
import com.example.kotlintask3.R
import com.example.kotlintask3.api.Item
import com.example.kotlintask3.databinding.FragmentSelectedItemBinding
import com.example.kotlintask3.model.ItemViewModel
import kotlinx.android.synthetic.main.fragment_selected_item.*
import kotlinx.coroutines.launch
import java.io.File


class SelectedItemFragment : Fragment(R.layout.fragment_selected_item) {
    private lateinit var viewModel: ItemViewModel
    private lateinit var binding: FragmentSelectedItemBinding
    private lateinit var downloadService: DownloadManager
    private val channelId = "channel_id_example_011"
    private val notificationId = 101
    var downloadId: Long = 0
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).viewModel
        requireContext().registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        binding = FragmentSelectedItemBinding.inflate(layoutInflater)
        createNotificationChannel()
        return binding.root
    }

    override fun onDestroyView() {
        requireContext().unregisterReceiver(broadcastReceiver)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postData()
    }

    private fun postData() {
        val id: String = arguments?.getString("id") ?: ""
        if (viewModel.hasInternetConnection()) {
            viewModel.getSelectedItem(id)
            viewModel.getItemLiveDataObserver().observe(this.viewLifecycleOwner) {
                (activity as AppCompatActivity).supportActionBar?.title = it.itemName
                Glide.with(binding.selectedItemImage).load(it.itemImage)
                    .into(binding.selectedItemImage)
                binding.selectedItemTitle.setText(it.itemName, TextView.BufferType.EDITABLE)
                binding.selectedItemDescription.text = it.ItemDescription
                progressBar?.visibility = View.GONE
                val item: Item = it
                val id1: String = it.id
                val url: String = it.itemImage
                val imageName: String = it.itemName
                binding.downloadButton.setOnClickListener {
                    downloadManager(url, imageName)
                }
                binding.updateNameButton.setOnClickListener {
                    updateName(id1, item)
                }
            }
        } else {
            viewModel.viewModelScope.launch {
                viewModel.getItemWhenNoConnection(id)
            }
            viewModel.getItemLiveDataObserver().observe(this.viewLifecycleOwner) {
                (activity as AppCompatActivity).supportActionBar?.title = it.itemName
                Glide.with(binding.selectedItemImage).load(R.drawable.no_image)
                    .into(binding.selectedItemImage)
                binding.selectedItemTitle.setText(it.itemName, TextView.BufferType.EDITABLE)
                val name =it.itemName
                binding.selectedItemDescription.text = it.ItemDescription
                binding.updateNameButton.setOnClickListener {
                    Toast.makeText(this.context,"Please Check Your Internet Connection To Update Name",Toast.LENGTH_LONG).show()
                    binding.selectedItemTitle.setText(name, TextView.BufferType.EDITABLE)
                }
                progressBar?.visibility = View.GONE
            }
        }
    }

    private fun downloadManager(url: String, imageName: String) {
        val filePath: String =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "KTask3"
        val folder = File(filePath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        downloadService = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request: DownloadManager.Request = DownloadManager.Request(url.toUri())
        request.setTitle(imageName)
            .setDescription("Image is downloading...")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/KTask3/$imageName"
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
        downloadId = downloadService.enqueue(request)
    }

    private fun updateName(id: String, item: Item) {

        val name: String = selectedItemTitle.text.toString()
        if (viewModel.hasInternetConnection()) {
            if (selectedItemTitle.text.isEmpty()) {
                Toast.makeText(this.context, "please Enter New Name", Toast.LENGTH_SHORT).show()
            } else {
                item.itemName = name
                viewModel.viewModelScope.launch {
                    viewModel.updateNameInApi(id, item)
                }
            }
        } else {
            if (selectedItemTitle.text.isEmpty()) {
                Toast.makeText(this.context, "please Enter New Name", Toast.LENGTH_SHORT).show()
            } else {
                item.itemName = name
                viewModel.viewModelScope.launch {
                    viewModel.updateNameInDatabase(id, name)
                }
            }
        }
        showNotification(name)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel =
                NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(name: String) {
        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.interess)
            .setContentTitle("Name Update")
            .setContentText("New name : ($name) is updated")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, builder.build())
        }
    }
}










