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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.kotlintask3.MainActivity
import com.example.kotlintask3.R
import com.example.kotlintask3.adapter.SelectedItemAdapter
import com.example.kotlintask3.api.Item
import com.example.kotlintask3.databinding.FragmentSelectedItemBinding
import com.example.kotlintask3.model.ItemViewModel
import kotlinx.android.synthetic.main.fragment_selected_item.*
import kotlinx.coroutines.launch
import java.io.File


class SelectedItemFragment : Fragment(R.layout.fragment_selected_item), UiFunction {
    private lateinit var viewModel: ItemViewModel
    private lateinit var binding: FragmentSelectedItemBinding
    private val channelId: String = "channel_id_example_011"
    private lateinit var downloadService: DownloadManager
    var downloadId: Long = 0
    private val notificationId: Int = 101
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
        if (!this::binding.isInitialized) {
            viewModel = (activity as MainActivity).viewModel
            requireContext().registerReceiver(
                broadcastReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
            binding = FragmentSelectedItemBinding.inflate(layoutInflater)
            postData()
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(binding.selectedItemRecyclerView)
            createNotificationChannel()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.nameUpdated.value == true)
            postData()
    }

    override fun onDestroyView() {
        requireContext().unregisterReceiver(broadcastReceiver)
        super.onDestroyView()
    }

    private fun postData() {
        val itemPosition: Int = arguments?.getInt("itemPosition", 0) ?: 0
        viewModel.position = itemPosition
        val linearLayoutManager = LinearLayoutManager(this.context)
        if (viewModel.hasInternetConnection()) {
            viewModel.data.observe(this.viewLifecycleOwner) {
                binding.selectedItemRecyclerView.layoutManager = LinearLayoutManager(
                    this.context, LinearLayoutManager.HORIZONTAL, false
                )
                binding.selectedItemRecyclerView.adapter =
                    SelectedItemAdapter(it, this@SelectedItemFragment)
                viewModel.insertData(it)
                binding.selectedItemRecyclerView.scrollToPosition(itemPosition)
                progressBar2.visibility = View.GONE
            }
            binding.selectedItemRecyclerView.scrollToPosition(itemPosition)
        } else {
            viewModel.viewModelScope.launch {
                viewModel.getDataFRomDb()
            }
            viewModel.data.observe(this.viewLifecycleOwner) {
                if (it == null) {
                    Log.d("No data In Database", it.toString())
                } else {
                    binding.selectedItemRecyclerView.layoutManager = linearLayoutManager
                    binding.selectedItemRecyclerView.adapter =
                        SelectedItemAdapter(it, this@SelectedItemFragment)
                    binding.selectedItemRecyclerView.scrollToPosition(itemPosition)
                }
            }
        }
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
                activity?.getSystemService(NotificationManager::class.java) as NotificationManager
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

    override fun downloadImage(url: String, imageName: String) {
        val filePath: String =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "KTask3"
        val folder = File(filePath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        downloadService =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request: DownloadManager.Request = DownloadManager.Request(url.toUri())
        request.setTitle(imageName)
            .setDescription("Image is downloading...")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/KTask3/$imageName.jpg"
            )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
        downloadId = downloadService.enqueue(request)
    }

    override fun updateName(
        id: String,
        item: Item,
        oldName: String,
        newName: String
    ) {
        if (viewModel.hasInternetConnection()) {
            if (oldName == newName) {
                Toast.makeText(context, "please Enter New Name", Toast.LENGTH_SHORT).show()
            } else {
                item.itemName = newName
                viewModel.viewModelScope.launch {
                    viewModel.updateNameInApi(id, item)
                    showNotification(newName)
                }
            }
        } else {
            Toast.makeText(context, "please Check Your Internet Connection", Toast.LENGTH_SHORT)
                .show()
        }
    }
}











