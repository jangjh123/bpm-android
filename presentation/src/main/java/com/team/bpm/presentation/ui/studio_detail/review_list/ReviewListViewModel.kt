package com.team.bpm.presentation.ui.studio_detail.review_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.team.bpm.domain.model.Review
import com.team.bpm.domain.usecase.review.*
import com.team.bpm.domain.usecase.splash.GetKakaoIdUseCase
import com.team.bpm.presentation.base.BaseViewModelV2
import com.team.bpm.presentation.model.BottomSheetButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val getKakaoIdUseCase: GetKakaoIdUseCase,
    private val reviewListUseCase: GetReviewListUseCase,
    private val likeReviewUseCase: LikeReviewUseCase,
    private val dislikeReviewUseCase: DislikeReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val reportReviewUseCase: ReportReviewUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModelV2(), ReviewListContract {

    private val _state = MutableStateFlow(ReviewListContract.State())
    override val state: StateFlow<ReviewListContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ReviewListContract.Effect>()
    override val effect: SharedFlow<ReviewListContract.Effect> = _effect.asSharedFlow()

    override fun event(event: ReviewListContract.Event) = when (event) {
        is ReviewListContract.Event.GetUserId -> {
            getUserId()
        }

        is ReviewListContract.Event.OnClickWriteReview -> {
            onClickWriteReview()
        }

        is ReviewListContract.Event.OnClickReviewActionButton -> {
            onClickReviewActionButton(event.review)
        }

        is ReviewListContract.Event.OnClickDeleteReview -> {
            onClickDeleteReview()
        }

        is ReviewListContract.Event.OnClickReportReview -> {
            onClickReportReview()
        }

        is ReviewListContract.Event.OnClickSendReviewReport -> {
            onClickSendReviewReport(event.reason)
        }

        is ReviewListContract.Event.OnClickReviewLike -> {
            onClickReviewLike(event.reviewId)
        }

        is ReviewListContract.Event.GetReviewList -> {
            getReviewList()
        }

        is ReviewListContract.Event.OnClickShowImageReviewsOnly -> {
            onClickShowImageReviewsOnly()
        }

        is ReviewListContract.Event.OnClickShowNotOnlyImageReviews -> {
            onClickShowNotOnlyImageReviews()
        }

        is ReviewListContract.Event.OnClickSortByLike -> {
            onClickSortByLike()
        }

        is ReviewListContract.Event.OnClickSortByDate -> {
            onClickSortByDate()
        }

        is ReviewListContract.Event.OnClickDismissReportDialog -> {
            onClickDismissReportDialog()
        }

        is ReviewListContract.Event.OnClickDismissNoticeDialog -> {
            onClickDismissNoticeDialog()
        }

        is ReviewListContract.Event.OnClickBackButton -> {
            onClickBackButton()
        }
    }

    private val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { coroutineContext, throwable ->

        }
    }

    private fun getUserId() {
        viewModelScope.launch(ioDispatcher) {
            getKakaoIdUseCase().onEach { userId ->
                withContext(mainImmediateDispatcher) {
                    _state.update {
                        it.copy(userId = userId)
                    }
                }
            }.launchIn(viewModelScope + exceptionHandler)
        }
    }

    private fun getStudioId(): Int? {
        return savedStateHandle.get<Int>(ReviewListActivity.KEY_STUDIO_ID)
    }

    private fun onClickWriteReview() {
        getStudioId()?.let { studioId ->
            viewModelScope.launch {
                _effect.emit(ReviewListContract.Effect.GoToWriteReview(studioId))
            }
        }
    }

    private fun onClickReviewActionButton(review: Review) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedReview = review,
                    bottomSheetButton = if (review.author?.id == state.value.userId) BottomSheetButton.DELETE_POST else BottomSheetButton.REPORT_POST,
                    isBottomSheetShowing = true
                )
            }
        }
    }

    private fun onClickDeleteReview() {
        getStudioId()?.let { studioId ->
            state.value.selectedReview?.id?.let { reviewId ->
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            isBottomSheetShowing = false
                        )
                    }

                    withContext(ioDispatcher) {
                        deleteReviewUseCase(studioId, reviewId).onEach {
                            withContext(mainImmediateDispatcher) {
                                _effect.emit(ReviewListContract.Effect.RefreshReviewList)
                            }
                        }.launchIn(viewModelScope + exceptionHandler)
                    }
                }
            }
        }
    }

    private fun onClickReportReview() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isReportDialogShowing = true,
                    isBottomSheetShowing = false
                )
            }
        }
    }

    private fun onClickSendReviewReport(reason: String) {
        getStudioId()?.let { studioId ->
            state.value.selectedReview?.id?.let { reviewId ->
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            isReportDialogShowing = false
                        )
                    }

                    withContext(ioDispatcher) {
                        reportReviewUseCase(studioId, reviewId, reason).onEach {
                            _state.update {
                                it.copy(isLoading = false)
                            }

                            _effect.emit(ReviewListContract.Effect.RefreshReviewList)
                        }.launchIn(viewModelScope + exceptionHandler)
                    }
                }
            }
        }
    }

    private fun onClickReviewLike(reviewId: Int) {
        getStudioId()?.let { studioId ->
            state.value.reviewList.find { review -> review.id == reviewId }?.let { selectedReview ->
                viewModelScope.launch(ioDispatcher) {
                    when (selectedReview.liked) {
                        true -> {
                            dislikeReviewUseCase(studioId, reviewId).onEach {
                                withContext(mainImmediateDispatcher) {
                                    _state.update {
                                        it.copy(reviewList = sortRefreshedReviewList(state.value.reviewList.toMutableList().apply {
                                            val targetIndex = indexOf(find { review -> review.id == reviewId })
                                            this[targetIndex] = this[targetIndex].copy(
                                                liked = false,
                                                likeCount = this[targetIndex].likeCount?.minus(1)
                                            )
                                        }))
                                    }
                                }
                            }.launchIn(viewModelScope + exceptionHandler)
                        }

                        false -> {
                            likeReviewUseCase(studioId, reviewId).onEach {
                                withContext(mainImmediateDispatcher) {
                                    _state.update {
                                        it.copy(reviewList = sortRefreshedReviewList(state.value.reviewList.toMutableList().apply {
                                            val targetIndex = indexOf(find { review -> review.id == reviewId })
                                            this[targetIndex] = this[targetIndex].copy(
                                                liked = true,
                                                likeCount = this[targetIndex].likeCount?.plus(1)
                                            )
                                        }))
                                    }
                                }
                            }.launchIn(viewModelScope + exceptionHandler)
                        }

                        null -> {
                            withContext(mainImmediateDispatcher) {
                                _effect.emit(ReviewListContract.Effect.ShowToast("좋아요 기능을 사용할 수 없습니다."))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getReviewList() {
        getStudioId()?.let { studioId ->
            viewModelScope.launch {
                _state.update {
                    it.copy(isLoading = true)
                }

                withContext(ioDispatcher) {
                    reviewListUseCase(studioId).onEach { result ->
                        withContext(mainImmediateDispatcher) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    originalReviewList = result.reviews ?: emptyList(),
                                    reviewList = result.reviews?.let { reviews -> sortRefreshedReviewList(reviews) } ?: emptyList()
                                )
                            }
                        }
                    }.launchIn(viewModelScope + exceptionHandler)
                }
            }
        }
    }

    private fun onClickShowImageReviewsOnly() {
        viewModelScope.launch {
            _state.update {
                val filteredList = state.value.originalReviewList.filter { review -> review.filesPath?.isNotEmpty() == true }
                it.copy(
                    isReviewListShowingImageReviewsOnly = true,
                    reviewList = if (state.value.isReviewListSortedByLike) filteredList.sortedByDescending { review -> review.likeCount }
                    else filteredList.sortedByDescending { review -> review.createdAt }
                )
            }
        }
    }

    private fun onClickShowNotOnlyImageReviews() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isReviewListShowingImageReviewsOnly = false,
                    reviewList = if (state.value.isReviewListSortedByLike) state.value.originalReviewList.sortedByDescending { review -> review.likeCount }
                    else state.value.originalReviewList.sortedByDescending { review -> review.createdAt }
                )
            }
        }
    }

    private fun onClickSortByLike() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    reviewList = state.value.reviewList.sortedByDescending { review -> review.likeCount },
                    isReviewListSortedByLike = true
                )
            }
        }
    }

    private fun onClickSortByDate() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    reviewList = state.value.reviewList.sortedByDescending { review -> review.createdAt },
                    isReviewListSortedByLike = false
                )
            }
        }
    }

    private fun sortRefreshedReviewList(list: List<Review>): List<Review> {
        val filteredList = if (state.value.isReviewListShowingImageReviewsOnly) {
            list.filter { it.filesPath?.isNotEmpty() == true }
        } else {
            list
        }

        return if (state.value.isReviewListSortedByLike) {
            filteredList.sortedByDescending { it.likeCount }
        } else {
            filteredList.sortedByDescending { it.createdAt }
        }
    }

    private fun onClickDismissReportDialog() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isReportDialogShowing = false
                )
            }
        }
    }

    private fun onClickDismissNoticeDialog() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isNoticeDialogShowing = false
                )
            }
        }
    }

    private fun onClickBackButton() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isBottomSheetShowing = false,
                    selectedReview = null
                )
            }
        }
    }
}