package com.example.kotlintask3.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.kotlintask3.MainActivity
import com.example.kotlintask3.R
import com.example.kotlintask3.adapter.ItemAdapter
import com.example.kotlintask3.databinding.FragmentItemListBinding
import com.example.kotlintask3.model.ItemViewModel
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.coroutines.launch


class ItemListFragment : Fragment(R.layout.fragment_item_list) {
    private lateinit var binding: FragmentItemListBinding
    private lateinit var viewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentItemListBinding.inflate(layoutInflater)
            viewModel = (activity as MainActivity).viewModel
            postData()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout = binding.slidingPaneLayout
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                ItemListOnBackPressedCallback(slidingPaneLayout)
            )
    }

    private fun postData() {
        val linearLayoutManager = LinearLayoutManager(this.context)
        if (viewModel.hasInternetConnection()) {
            viewModel.data.observe(this.viewLifecycleOwner) {
                binding.itemsRecyclerView.layoutManager = linearLayoutManager
                binding.itemsRecyclerView.adapter = ItemAdapter(it)
                viewModel.insertData(it)
                progressBar.visibility = View.GONE
            }
        } else {
            viewModel.viewModelScope.launch {
                viewModel.getDataFRomDb()
            }
            viewModel.data.observe(this.viewLifecycleOwner) {
                if (it == null) {
                    Log.d("No data In Database", it.toString())
                } else {
                    binding.itemsRecyclerView.layoutManager = linearLayoutManager
                    binding.itemsRecyclerView.adapter = ItemAdapter(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "items"
        if (viewModel.nameUpdated.value == true)
            postData()
        binding.itemsRecyclerView.scrollToPosition(viewModel.position)
    }
}

class ItemListOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout
) : OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen),
    SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        slidingPaneLayout.closePane()
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {
    }

    override fun onPanelOpened(panel: View) {
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        isEnabled = false
    }
}