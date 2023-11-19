package com.dicoding.musicstory.ui.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.provider.Settings
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.dicoding.musicstory.databinding.FragmentProfileBinding
import com.dicoding.musicstory.response.LoginModel
import com.dicoding.musicstory.preference.LoginPreference
import com.dicoding.musicstory.ui.login.LoginAct

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoginPreference = LoginPreference(requireContext())
        loginModel = mLoginPreference.getUser()

        playAnimation()
        setupUi()
        languageHandler()
        logoutHandler()
        setHasOptionsMenu(true)
    }

    private fun setupUi() {
        binding.nameTextView.text = loginModel.name
    }

    private fun languageHandler() {
        binding.languageCardView.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun logoutHandler() {
        binding.logoutCardView.setOnClickListener {
            mLoginPreference.removeUser()
            val intent = Intent(activity, LoginAct::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun playAnimation() {
        val userDetailCardView = ObjectAnimator.ofFloat(binding.userDetailCardView, View.ALPHA, 1f).setDuration(500)
        val settingTextView = ObjectAnimator.ofFloat(binding.settingTextView, View.TRANSLATION_X, 1f, 0f).setDuration(500)
        val languageCardView = ObjectAnimator.ofFloat(binding.languageCardView, View.ALPHA, 1f).setDuration(500)
        val logoutCardView = ObjectAnimator.ofFloat(binding.logoutCardView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playTogether(userDetailCardView, settingTextView, languageCardView, logoutCardView)
            start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the back button click here
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
