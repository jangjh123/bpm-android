package com.team.bpm.presentation.ui.studio_detail.review_detail

import com.team.bpm.domain.model.Review
import com.team.bpm.presentation.base.BaseContract
import com.team.bpm.presentation.model.BottomSheetButton

interface ReviewDetailContract : BaseContract<ReviewDetailContract.State, ReviewDetailContract.Event, ReviewDetailContract.Effect> {
    data class State(
        val userId: Long? = null,
        val isLoading: Boolean = false,
        val review: Review? = null,
        val liked: Boolean? = false,
        val likeCount: Int? = null,
        val isBottomSheetShowing: Boolean = false,
        val bottomSheetButtonList: List<BottomSheetButton> = emptyList(),
        val isReportDialogShowing: Boolean = false,
        val isNoticeDialogShowing: Boolean = false,
        val noticeDialogContent: String = "신고가 완료되었습니다."
    )

    sealed interface Event {
        object GetUserId : Event

        object OnClickReviewActionButton : Event

        object OnClickDeleteReview : Event

        object OnClickReportReview : Event

        data class OnClickSendReviewReport(val reason: String) : Event

        object OnClickLike : Event

        object GetReviewDetail : Event

        object OnClickDismissNoticeDialog : Event

        object OnClickDismissReportDialog : Event

        object OnBottomSheetHide : Event
    }

    sealed interface Effect {
        data class ShowToast(val text: String) : Effect

        object GoBack : Effect
    }
}