package com.prodev.muslimq.presentation.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.prodev.muslimq.R
import com.prodev.muslimq.databinding.FragmentOnBoardingBinding
import com.prodev.muslimq.presentation.adapter.OnBoardingAdapter
import com.prodev.muslimq.presentation.adapter.OnBoardingAdapter.OnBoardingItem
import com.prodev.muslimq.presentation.viewmodel.DataStoreViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>(
    FragmentOnBoardingBinding::inflate
) {

    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val onBoardingAdapter by lazy { OnBoardingAdapter() }
    private var checkState: OnPageChangeCallback? = null

    private lateinit var onBackPressedDispatcher: OnBackPressedDispatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav).isVisible = false

        val data = listOf(
            OnBoardingItem(
                R.drawable.quran_onboard,
                "Membaca Al-Qur'an",
                "Menikmati membaca Al-Qur'an dengan mudah dalam genggaman Smartphone. Jelajahi berbagai fitur yang tersedia seperti tafsir, & lainnya"
            ),
            OnBoardingItem(
                R.drawable.adzan_onboard,
                "Waktu Shalat & Arah Kiblat",
                "Dapatkan waktu shalat yang akurat & tepat waktu sesuai lokasi anda berada, serta arah kiblat yang mudah dan efisien untuk di akses kapanpun"
            ),
            OnBoardingItem(
                R.drawable.doa_onboard,
                "Bacaan Doa Harian & Dzikir",
                "Temukan koleksi doa harian yang bisa Anda baca atau pelajari, & amalkan dalam kehidupan sehari-hari. Serta anda bisa berdzikir dengan fitur tasbih yang terdapat pada aplikasi ini"
            )
        )
        onBoardingAdapter.setOnBoardingItem(data)

        binding.apply {
            vpOnboard.adapter = onBoardingAdapter
            vpOnboard.offscreenPageLimit = 1
            dotsIndicator.setViewPager2(vpOnboard)

            onBackPressedDispatcher = requireActivity().onBackPressedDispatcher
            onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (getItem() == 0) {
                            isEnabled = false
                            requireActivity().finish()
                        } else {
                            vpOnboard.setCurrentItem(getItem() - 1, true)
                        }
                    }
                })

            checkState = object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val lastPosition = onBoardingAdapter.itemCount - 1
                    val context = requireContext()

                    val iconDrawable = ContextCompat.getDrawable(
                        context, if (position == lastPosition) {
                            R.drawable.ic_check
                        } else {
                            R.drawable.ic_arrow_right
                        }
                    )
                    fabOnBoarding.icon = iconDrawable

                    val backgroundTint = ContextCompat.getColorStateList(
                        context, if (position == lastPosition) {
                            R.color.green_base
                        } else {
                            R.color.blue_night
                        }
                    )
                    fabOnBoarding.backgroundTintList = backgroundTint

                    if (position == lastPosition) {
                        fabOnBoarding.extend()
                    } else {
                        fabOnBoarding.shrink()
                    }
                }
            }
            vpOnboard.registerOnPageChangeCallback(checkState as OnPageChangeCallback)

            fabOnBoarding.setOnClickListener {
                if (getItem() == onBoardingAdapter.itemCount - 1) {
                    dataStoreViewModel.saveOnboardingState(true)
                    vpOnboard.unregisterOnPageChangeCallback(checkState as OnPageChangeCallback)
                    findNavController().popBackStack()
                    root.visibility = View.GONE
                } else {
                    vpOnboard.setCurrentItem(getItem() + 1, true)
                }
            }
        }
    }

    private fun getItem(): Int = binding.vpOnboard.currentItem

    override fun onDestroyView() {
        super.onDestroyView()
        !onBackPressedDispatcher.hasEnabledCallbacks()
    }
}