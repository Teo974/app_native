package com.example.aplicaciones_nativas.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicaciones_nativas.data.Moment
import com.example.aplicaciones_nativas.data.MomentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(private val momentDao: MomentDao) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery

    val moments: StateFlow<List<Moment>> = _searchQuery.flatMapLatest {
        query ->
        if (query.isBlank()) {
            momentDao.getAllMoments()
        } else {
            momentDao.getMomentsWithTag("%#$query%")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteMoment(moment: Moment) {
        viewModelScope.launch {
            momentDao.delete(moment)
        }
    }
}
