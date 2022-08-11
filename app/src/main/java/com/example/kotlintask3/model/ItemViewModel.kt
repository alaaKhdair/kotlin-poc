package com.example.kotlintask3.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlintask3.ItemApplication
import com.example.kotlintask3.api.Item
import com.example.kotlintask3.api.RetrofitInstance
import com.example.kotlintask3.repository.ItemRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ItemViewModel(
    private val itemRepository: ItemRepository,
    app: Application
) : AndroidViewModel(app) {

    private val _data = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> = _data

    private val _item = MutableLiveData<Item>()
    val item: LiveData<Item> = _item

    private val _nameUpdated = MutableLiveData<Boolean>()
    val nameUpdated: LiveData<Boolean> = _nameUpdated

    var position: Int = 0


    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            val call = RetrofitInstance.retrofitService.getData()
            call.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                    _data.postValue(response.body())
                }

                override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                    viewModelScope.launch {
                        getDataFRomDb()
                    }
                }
            })
        }
    }

    fun getSelectedItem(id: String) {
        val call = RetrofitInstance.retrofitService.getSelectedItemData(id)
        call.enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                _item.postValue(response.body())
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                viewModelScope.launch { getSelectedItem(id) }
            }
        })
    }

    fun insertData(items: List<Item>) = viewModelScope.launch {
        for (item in items) {
            itemRepository.insertItems(item)
        }
    }

    suspend fun getDataFRomDb() = _data.postValue(itemRepository.getData())

    fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<ItemApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun updateNameInDatabase(id: String, name: String) = viewModelScope.launch {
        itemRepository.updateName(id, name)
    }

    fun updateNameInApi(id: String, item: Item) {
        val call = RetrofitInstance.retrofitService.updateName(id, item)
        call.enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                getSelectedItem(id)
                updateNameInDatabase(id, item.itemName)
                _nameUpdated.value = true
                getItems()
            }
            override fun onFailure(call: Call<Item>, t: Throwable) {
                Log.d("UPDATE NAME", "failed to update")
                _nameUpdated.value = false
            }
        })
    }

}










