package com.team.bpm.presentation.ui.main.mypage

import androidx.fragment.app.viewModels
import com.team.bpm.presentation.base.BaseFragment
import com.team.bpm.presentation.databinding.FragmentMypageBinding
import com.team.bpm.presentation.ui.main.mypage.myquestion.MyQuestionActivity
import com.team.bpm.presentation.ui.main.mypage.notification.MyPageNotificationActivity
import com.team.bpm.presentation.ui.main.mypage.starttab.MyPageStartTabActivity
import com.team.bpm.presentation.util.repeatCallDefaultOnStarted
import com.team.bpm.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {

    override val viewModel: MyPageViewModel by viewModels()

    override fun initLayout() {
        bind {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun setupCollect() {
        repeatCallDefaultOnStarted {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is MyPageContract.Effect.ShowToast -> {
                        requireContext().showToast(effect.text)
                    }
                    MyPageContract.Effect.GoNotification -> {
                        // 알림 페이지 이동
                        goToNotification()
                    }
                    MyPageContract.Effect.GoMyPost -> {
                        // 내가 작성한 커뮤니티 글
                    }
                    MyPageContract.Effect.GoScrappedStudios -> {
                        // 스크랩한 스튜디오 리스트
                    }
                    MyPageContract.Effect.GoProfileManage -> {
                        // 프로필 관리 페이지 이동
                    }
                    MyPageContract.Effect.GoMyQuestion -> {
                        // 질문 모아보기
                        goToMyQuestion()
                    }
                    MyPageContract.Effect.GoEditStartTab -> {
                        goToEditStartTab()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getMyPageInfo()
    }

    private fun goToNotification() {
        startActivity(MyPageNotificationActivity.newIntent(requireContext()))
    }

    private fun goToMyQuestion() {
        startActivity(MyQuestionActivity.newIntent(requireContext()))
    }

    private fun goToEditStartTab() {
        startActivity(MyPageStartTabActivity.newIntent(requireContext()))
    }

    companion object {

        fun newInstance(): MyPageFragment {
            return MyPageFragment()
        }
    }
}