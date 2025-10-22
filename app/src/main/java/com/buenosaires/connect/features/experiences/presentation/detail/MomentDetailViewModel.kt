package com.buenosaires.connect.features.experiences.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenosaires.connect.core.data.CommentDao
import com.buenosaires.connect.core.data.MomentDao
import com.buenosaires.connect.core.model.Comment
import com.buenosaires.connect.core.model.Moment
import com.buenosaires.connect.features.experiences.domain.MomentDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MomentDetailViewModel @Inject constructor(
    private val momentDao: MomentDao,
    private val commentDao: CommentDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val momentId: Long = checkNotNull(savedStateHandle["momentId"]) {
        "momentId argument is required"
    }

    private val defaultMoment = MomentDefaults.findById(momentId)
    private val currentAuthor = "Tú"

    private val momentFlow: Flow<Moment?> = if (momentId <= 0) {
        flowOf(defaultMoment)
    } else {
        momentDao.getMomentById(momentId).map { it as Moment }
    }

    val moment: StateFlow<Moment?> = momentFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = defaultMoment
    )

    val comments: StateFlow<List<Comment>> = commentDao
        .getCommentsForMoment(momentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addComment(content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            commentDao.insert(
                Comment(
                    momentId = momentId,
                    author = currentAuthor,
                    content = trimmed,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteComment(commentId: Long) {
        if (commentId <= 0) return
        viewModelScope.launch {
            commentDao.deleteById(commentId)
        }
    }

    fun isOwnComment(comment: Comment): Boolean = comment.author == currentAuthor
}

