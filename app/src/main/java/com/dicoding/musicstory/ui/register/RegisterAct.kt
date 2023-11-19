package com.dicoding.musicstory.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.dicoding.musicstory.R
import com.dicoding.musicstory.customview.CustNotif
import com.dicoding.musicstory.data.Result
import com.dicoding.musicstory.databinding.ActivityRegisterBinding
import com.dicoding.musicstory.utils.FactoryVM
import com.dicoding.musicstory.utils.isValidEmail
import com.dicoding.musicstory.utils.validateMinLength

class RegisterAct : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: FactoryVM
    private val registerViewModel: RegisterVM by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        playAnimation()
        setupToolbar()
        backButtonHandler()
        registerButtonHandler()
        setMyButtonEnable()
        emailEditTextHandler()
        nameEditTextHandler()
        passwordEditTextHandler()
        confirmationPasswordEditTextHandler()
    }

    private fun setupViewModel() {
        factory = FactoryVM.getInstance(binding.root.context)
    }

    private fun setupToolbar() {
        title = resources.getString(R.string.register_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun backButtonHandler() {
        binding.registerLayout.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadingHandler(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.root.visibility = View.VISIBLE
            binding.registerLayout.root.visibility = View.GONE
        } else {
            binding.loadingLayout.root.visibility = View.GONE
            binding.registerLayout.root.visibility = View.VISIBLE
        }
    }

    private fun errorHandler() {
        CustNotif(this, R.string.error_message, R.drawable.error).show()
    }

    private fun registerHandler() {
        CustNotif(this, R.string.success_create_user, R.drawable.user_created).show()
        binding.registerLayout.emailEditText.text?.clear()
        binding.registerLayout.passwordEditText.text?.clear()
        binding.registerLayout.nameEditText.text?.clear()
        binding.registerLayout.confirmPasswordEditText.text?.clear()
    }

    private fun registerButtonHandler() {
        binding.registerLayout.registerButton.setOnClickListener {
            val email = binding.registerLayout.emailEditText.text.toString()
            val password = binding.registerLayout.passwordEditText.text.toString()
            val name = binding.registerLayout.nameEditText.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)) {
                handlingRegister(name, email, password)
            } else {
                CustNotif(this, R.string.error_validation, R.drawable.error_form).show()
            }
        }
    }

    private fun handlingRegister(name: String, email: String, password: String) {
        registerViewModel.postRegister(name, email, password).observe(this@RegisterAct) { result ->
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
                        loadingHandler(false)
                        registerHandler()
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val emailTextView = ObjectAnimator.ofFloat(binding.registerLayout.emailTextView, View.ALPHA, 1f).setDuration(250)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.registerLayout.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val emailEditText = ObjectAnimator.ofFloat(binding.registerLayout.emailEditText, View.ALPHA, 1f).setDuration(250)

        val nameTextView = ObjectAnimator.ofFloat(binding.registerLayout.nameTextView, View.ALPHA, 1f).setDuration(250)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.registerLayout.nameEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val nameEditText = ObjectAnimator.ofFloat(binding.registerLayout.nameEditText, View.ALPHA, 1f).setDuration(250)

        val passwordTextView = ObjectAnimator.ofFloat(binding.registerLayout.passwordTextView, View.ALPHA, 1f).setDuration(250)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.registerLayout.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val passwordEditText = ObjectAnimator.ofFloat(binding.registerLayout.passwordEditText, View.ALPHA, 1f).setDuration(250)

        val confirmPasswordTextView = ObjectAnimator.ofFloat(binding.registerLayout.confirmPasswordTextView, View.ALPHA, 1f).setDuration(250)
        val confirmPasswordEditTextLayout = ObjectAnimator.ofFloat(binding.registerLayout.confirmPasswordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val confirmPasswordEditText = ObjectAnimator.ofFloat(binding.registerLayout.confirmPasswordEditText, View.ALPHA, 1f).setDuration(250)

        val loginButton = ObjectAnimator.ofFloat(binding.registerLayout.registerButton, View.ALPHA, 1f).setDuration(150)
        val backButton = ObjectAnimator.ofFloat(binding.registerLayout.backButton, View.ALPHA, 1f).setDuration(150)

        val together = AnimatorSet().apply {
            playTogether(loginButton, backButton)
        }

        AnimatorSet().apply {
            playSequentially(
                emailTextView, emailEditTextLayout, emailEditText,
                nameTextView, nameEditTextLayout, nameEditText,
                passwordTextView, passwordEditTextLayout, passwordEditText,
                confirmPasswordTextView, confirmPasswordEditTextLayout, confirmPasswordEditText,
                together
            )
            start()
        }
    }

    private fun setMyButtonEnable() {
        val emailEditText = binding.registerLayout.emailEditText.text
        val passwordEditText = binding.registerLayout.passwordEditText.text
        val nameEditText = binding.registerLayout.nameEditText.text
        val confirmationEditText = binding.registerLayout.confirmPasswordEditText.text
        binding.registerLayout.registerButton.isEnabled =
            isValidEmail(emailEditText.toString()) && validateMinLength(nameEditText.toString()) && validateMinLength(passwordEditText.toString()) && confirmationEditText.toString() == passwordEditText.toString()
    }

    private fun emailEditTextHandler() {
        val emailEditText = binding.registerLayout.emailEditText
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun nameEditTextHandler() {
        val nameEditText = binding.registerLayout.nameEditText
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (validateMinLength(nameEditText.text.toString())) {
                    nameEditText.error = null
                } else {
                    nameEditText.error = getString(R.string.invalid_name)
                }
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun passwordEditTextHandler() {
        binding.registerLayout.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun confirmationPasswordEditTextHandler() {
        binding.registerLayout.confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}