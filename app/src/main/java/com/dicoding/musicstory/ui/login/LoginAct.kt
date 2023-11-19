package com.dicoding.musicstory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.dicoding.musicstory.R
import com.dicoding.musicstory.customview.CustNotif
import com.dicoding.musicstory.data.Result
import com.dicoding.musicstory.ui.register.RegisterAct
import com.dicoding.musicstory.databinding.ActivityLoginBinding
import com.dicoding.musicstory.response.LoginModel
import com.dicoding.musicstory.response.LoginResponse
import com.dicoding.musicstory.preference.LoginPreference
import com.dicoding.musicstory.ui.mainmenu.MainAct
import com.dicoding.musicstory.utils.FactoryVM
import com.dicoding.musicstory.utils.isValidEmail
import com.dicoding.musicstory.utils.validateMinLength

class LoginAct : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: FactoryVM
    private val loginViewModel: LoginVM by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        playAnimation()
        hideActionBar()
        registerButtonHandler()
        loginButtonHandler()
        setMyButtonEnable()
        emailEditTextHandler()
        passwordEditTextHandler()
    }

    private fun setupViewModel() {
        factory = FactoryVM.getInstance(binding.root.context)
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    private fun registerButtonHandler() {
        binding.loginLayout.registerButton.setOnClickListener {
            val intent = Intent(this@LoginAct, RegisterAct::class.java)
            startActivity(intent)
        }
    }

    private fun loginButtonHandler() {
        binding.loginLayout.loginButton.setOnClickListener {
            val email = binding.loginLayout.emailEditText.text.toString()
            val password = binding.loginLayout.passwordEditText.text.toString()

            if (!isEmpty(email) && !isEmpty(password)) {
                handlingLogin(email, password)
            } else {
                CustNotif(this, R.string.error_validation, R.drawable.error_form).show()
            }
        }
    }

    private fun handlingLogin(email: String, password: String) {
        loginViewModel.postLogin(email, password).observe(this@LoginAct) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        loadingHandler(true)
                    }
                    is Result.Error -> {
                        loadingHandler(false)
                        errorHandler()
                    }
                    is Result.Success -> {
                        successLoginHandler(result.data)
                    }
                }
            }
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.root.visibility = View.VISIBLE
            binding.loginLayout.root.visibility = View.GONE
        } else {
            binding.loadingLayout.root.visibility = View.GONE
            binding.loginLayout.root.visibility = View.VISIBLE
        }
    }

    private fun successLoginHandler(loginResponse: LoginResponse) {
        saveLoginData(loginResponse)
        navigateToHome()
    }

    private fun errorHandler() {
        CustNotif(this, R.string.error_message, R.drawable.error).show()
    }

    private fun saveLoginData(loginResponse: LoginResponse) {
        val loginPreference = LoginPreference(this)
        val loginResult = loginResponse.loginResult
        val loginModel = LoginModel(
            name = loginResult?.name, userId = loginResult?.userId, token = loginResult?.token
        )

        loginPreference.setLogin(loginModel)
    }

    private fun navigateToHome() {
        val intent = Intent(this@LoginAct, MainAct::class.java)
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.loginLayout.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailTextView = ObjectAnimator.ofFloat(binding.loginLayout.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.loginLayout.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailEditText = ObjectAnimator.ofFloat(binding.loginLayout.emailEditText, View.ALPHA, 1f).setDuration(500)

        val passwordTextView = ObjectAnimator.ofFloat(binding.loginLayout.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.loginLayout.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordEditText = ObjectAnimator.ofFloat(binding.loginLayout.passwordEditText, View.ALPHA, 1f).setDuration(500)

        val loginButton = ObjectAnimator.ofFloat(binding.loginLayout.loginButton, View.ALPHA, 1f).setDuration(500)
        val registerButton = ObjectAnimator.ofFloat(binding.loginLayout.registerButton, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(loginButton, registerButton)
        }

        AnimatorSet().apply {
            playSequentially(emailTextView, emailEditTextLayout, emailEditText, passwordTextView, passwordEditTextLayout, passwordEditText, together)
            start()
        }
    }

    private fun setMyButtonEnable() {
        val emailEditText = binding.loginLayout.emailEditText.text
        val passwordEditText = binding.loginLayout.passwordEditText.text
        binding.loginLayout.loginButton.isEnabled =
            isValidEmail(emailEditText.toString()) && validateMinLength(passwordEditText.toString())
    }

    private fun emailEditTextHandler() {
        val emailEditText = binding.loginLayout.emailEditText
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun passwordEditTextHandler() {
        binding.loginLayout.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}