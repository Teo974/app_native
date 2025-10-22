package com.buenosaires.connect.features.experiences.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.core.data.CommentDao
import com.buenosaires.connect.core.data.MomentDao
import com.buenosaires.connect.core.model.Comment
import com.buenosaires.connect.core.model.Moment
import com.buenosaires.connect.features.experiences.domain.MomentDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val momentDao: MomentDao,
    private val commentDao: CommentDao
) : ViewModel() {

    private val defaultCommunityPosts: List<Moment> = MomentDefaults.communityPosts

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val commentsByMoment: StateFlow<Map<Long, List<Comment>>> = commentDao
        .getAllComments()
        .map { comments -> comments.groupBy { it.momentId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val moments: StateFlow<List<Moment>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                momentDao.getAllMoments()
                    .map { existing ->
                        (defaultCommunityPosts + existing).sortedByDescending { it.date }
                    }
            } else {
                val trimmedQuery = query.trim()
                momentDao.getMomentsWithTag("%${trimmedQuery}%")
                    .map { existing ->
                        val matchesDefaults = defaultCommunityPosts.filter { post ->
                            post.description.contains(trimmedQuery, ignoreCase = true) ||
                                post.location.contains(trimmedQuery, ignoreCase = true)
                        }
                        (matchesDefaults + existing).sortedByDescending { it.date }
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteMoment(moment: Moment) {
        if (moment.id <= 0) return
        viewModelScope.launch {
            momentDao.delete(moment)
        }
    }

    fun addComment(momentId: Long, author: String, content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            commentDao.insert(
                Comment(
                    momentId = momentId,
                    author = author,
                    content = trimmed,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
