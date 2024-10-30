package com.example.aicomsapp.viewmodels.shared

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedUserViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Untuk menyimpan UID pengguna
    private var currentUID: String? = null

    private val _userStatus = MutableLiveData<Int>()
    val userStatus: LiveData<Int> get() = _userStatus

    fun setUserStatus(status: Int) {
        _userStatus.value = status
    }
    // LiveData untuk note dan to-do list
    private val _note = MutableLiveData<String>()
    val note: LiveData<String> get() = _note

    private val _todoList = MutableLiveData<MutableList<String>>()
    val todoList: LiveData<MutableList<String>> get() = _todoList

    // Fungsi untuk mengubah UID pengguna saat login
    fun setUID(uid: String) {
        currentUID = uid
        loadDataForUID(uid)
    }

    // Fungsi untuk memuat data berdasarkan UID
    private fun loadDataForUID(uid: String) {
        _note.value = sharedPreferences.getString("note_$uid", "")
        _todoList.value = sharedPreferences.getStringSet("todo_list_$uid", setOf())?.toMutableList() ?: mutableListOf()
    }

    // Fungsi untuk menyimpan data berdasarkan UID
    private fun saveDataForUID(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString("note_$uid", _note.value)
        editor.putStringSet("todo_list_$uid", _todoList.value?.toSet())
        editor.apply()
    }

    // Fungsi untuk mengatur dan menyimpan note
    fun setNote(note: String) {
        _note.value = note
        currentUID?.let { saveDataForUID(it) }
    }

    // Fungsi untuk menambahkan task ke To-Do List dan menyimpan
    fun addTodoItem(task: String) {
        val currentList = _todoList.value ?: mutableListOf()
        currentList.add(task)
        _todoList.value = currentList
        currentUID?.let { saveDataForUID(it) }
    }

    // Fungsi untuk menghapus task dari To-Do List
    fun removeTodoItem(task: String) {
        val currentList = _todoList.value ?: mutableListOf()
        currentList.remove(task)
        _todoList.value = currentList
        currentUID?.let { saveDataForUID(it) }
    }
}
