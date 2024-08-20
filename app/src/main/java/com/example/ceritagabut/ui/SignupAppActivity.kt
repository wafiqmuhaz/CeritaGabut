package com.example.ceritagabut.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ceritagabut.R
import com.example.ceritagabut.databinding.ActivitySignupAppBinding
import com.example.ceritagabut.factory.AppFactory
import com.example.ceritagabut.models.AppSignUpViewModel

class SignupAppActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivitySignupAppBinding

    private val factory: AppFactory = AppFactory.getInstance(this)
    private val signupViewModel: AppSignUpViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivitySignupAppBinding.inflate(layoutInflater)
        setContentView(appBinding.root)
        supportActionBar?.hide()
        setupAction()
        animationAction()
    }

    private fun animationAction() {
        val duration = 500L
        val startDelay = 500L
        val interpolator = AccelerateDecelerateInterpolator()

        val ivSignupAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.ivSignupApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val tvRegisterNameAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.tvRegisterNameApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val edRegisterNameAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.edRegisterName, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val tvRegisterEmailAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.tvRegisterEmailApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val edRegisterEmailAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.edRegisterEmail, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val tvRegisterPasswordAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.tvRegisterPasswordApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val edRegisterPasswordAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.edRegisterPassword, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val signupAlphaAnimator =
            ObjectAnimator.ofFloat(appBinding.signupButtonApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val rotationAnimator =
            ObjectAnimator.ofFloat(appBinding.signupButtonApp, View.ROTATION, 0f, 360f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val colorAnimator =
            ObjectAnimator.ofArgb(appBinding.signupButtonApp.background, "tint", Color.BLUE, Color.RED)
                .apply {
                    this.duration = duration
                    this.interpolator = interpolator
                }

        val translationYAnimator =
            ObjectAnimator.ofFloat(appBinding.signupButtonApp, View.TRANSLATION_Y, 0f, 100f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        AnimatorSet().apply {
            playSequentially(
                ivSignupAlphaAnimator, tvRegisterNameAlphaAnimator, edRegisterNameAlphaAnimator,
                tvRegisterEmailAlphaAnimator, edRegisterEmailAlphaAnimator,
                tvRegisterPasswordAlphaAnimator, edRegisterPasswordAlphaAnimator,
                signupAlphaAnimator, rotationAnimator, colorAnimator, translationYAnimator
            )
            this.startDelay = startDelay
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            appBinding.progressBarRegisterApp.visibility = View.VISIBLE
            appBinding.progressBarRegisterApp.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        appBinding.progressBarRegisterApp.visibility = View.VISIBLE
                    }
                })
        } else {
            appBinding.progressBarRegisterApp.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        appBinding.progressBarRegisterApp.visibility = View.GONE
                    }
                })
            appBinding.progressBarRegisterApp.visibility = View.GONE
        }
    }


    private fun setupAction() {
        appBinding.signupButtonApp.setOnClickListener {
            val name = appBinding.edRegisterName.text.toString()
            val email = appBinding.edRegisterEmail.text.toString()
            val password = appBinding.edRegisterPassword.text.toString()
            val duration = 500L
            val interpolator = AccelerateDecelerateInterpolator()

            val nameErrorAnimator = ObjectAnimator.ofFloat(
                appBinding.edRegisterName,
                View.TRANSLATION_X,
                -10f,
                10f,
                -10f,
                10f,
                -5f,
                5f,
                0f
            ).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

            val emailErrorAnimator = ObjectAnimator.ofFloat(
                appBinding.edRegisterEmail,
                View.TRANSLATION_X,
                -10f,
                10f,
                -10f,
                10f,
                -5f,
                5f,
                0f
            ).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

            val passwordErrorAnimator = ObjectAnimator.ofFloat(
                appBinding.edRegisterPassword,
                View.TRANSLATION_X,
                -10f,
                10f,
                -10f,
                10f,
                -5f,
                5f,
                0f
            ).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

            val shakeAnimatorSet = AnimatorSet().apply {
                playTogether(nameErrorAnimator, emailErrorAnimator, passwordErrorAnimator)
            }

            shakeAnimatorSet.start()

            when {
                name.isEmpty() -> {
                    appBinding.edRegisterName.error = getString(R.string.user_input_name)
                }

                email.isEmpty() -> {
                    appBinding.edRegisterEmail.error = getString(R.string.user_input_email)
                }

                password.isEmpty() -> {
                    appBinding.edRegisterPassword.error = getString(R.string.user_input_password)
                }

                password.length < 8 -> {
                    appBinding.edRegisterPassword.error = getString(R.string.user_invalid_password)
                }

                else -> {
                    signupViewModel.signup(name, email, password).observe(this) { result ->
                        val successMessage = getString(R.string.user_register_success)
                        val failedMessage = getString(R.string.user_register_failed)

                        if (result.appMessage == "201") {
                            showSuccessDialog(successMessage)
                        } else if (result.appMessage == "400") {
                            showErrorDialog(failedMessage)
                        } else {
                            showLoading(result.appMessage.isEmpty())
                        }
                    }
                }
            }
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_info)
        builder.setMessage(errorMessage)
        builder.setIcon(R.drawable.baseline_face_retouching_off_red_24)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 2000)
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_info)
        builder.setMessage(message)
        builder.setIcon(R.drawable.baseline_person_outline_24)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
            val intent = Intent(this, SigningAppActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }


}