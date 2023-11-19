package com.dicoding.musicstory.ui.splash_screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.musicstory.R
import com.dicoding.musicstory.constants.Constants
import com.dicoding.musicstory.databinding.ActivitySplashScreenBinding
import com.dicoding.musicstory.response.LoginModel
import com.dicoding.musicstory.preference.LoginPreference
import com.dicoding.musicstory.ui.login.LoginAct
import com.dicoding.musicstory.ui.mainmenu.MainAct

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@SuppressLint("CustomSplashScreen")
class SplashScreenAct : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var mLoginPreference: LoginPreference
    private lateinit var loginModel: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreference = LoginPreference(this)

        loginModel = mLoginPreference.getUser()

        splashScreenHandler()
    }

    private fun splashScreenHandler() {
        if (loginModel.name != null && loginModel.userId != null && loginModel.token != null) {
            val intent = Intent(this, MainAct::class.java)
            navigateWithAnimation(intent)
        } else {
            val intent = Intent(this, LoginAct::class.java)
            navigateWithAnimation(intent)
        }
    }

    private fun navigateWithAnimation(intent: Intent) {
        val transition = AutoTransition()
        transition.duration = 1000 // Duration of the animation in milliseconds
        transition.addTransition(ChangeBounds())
        transition.addTransition(ChangeImageTransform())

        val constraintLayout = findViewById<ConstraintLayout>(R.id.motionlayout)
        TransitionManager.beginDelayedTransition(constraintLayout, transition)

        val params = binding.logostories.layoutParams as ConstraintLayout.LayoutParams
        params.verticalBias = 0.2f
        binding.logostories.layoutParams = params

        val splashTimer: Long = Constants.SPLASH_SCREEN_TIMER
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, splashTimer)
    }
}
