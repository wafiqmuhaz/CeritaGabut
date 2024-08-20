package com.example.ceritagabut.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ceritagabut.MainActivity
import com.example.ceritagabut.R
import com.example.ceritagabut.databinding.ActivitySigningAppBinding
import com.example.ceritagabut.factory.AppFactory
import com.example.ceritagabut.models.AppSigninViewModel

private val Context.dataStores: DataStore<Preferences> by preferencesDataStore(name = "Person")

class SigningAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigningAppBinding

    private val appFactory: AppFactory = AppFactory.getInstance(this)
    private val signinAppViewModel: AppSigninViewModel by viewModels {
        appFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigningAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAppAnimation()
        animationAction()
        haveAccount()

    }

    private fun haveAccount() {
        binding.haveAccountTextViewApp.setOnClickListener {
            val intent = Intent(this, SignupAppActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAppAnimation() {
        val duration = 500L
        val startDelay = 500L
        val interpolator = AccelerateDecelerateInterpolator()

        val ivSigninAlphaAnimator =
            ObjectAnimator.ofFloat(binding.ivSigninApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val tvLoginEmailAlphaAnimator =
            ObjectAnimator.ofFloat(binding.tvLoginEmailApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val edLoginEmailAlphaAnimator =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val tvEdLoginPasswordAlphaAnimator =
            ObjectAnimator.ofFloat(binding.tvEdLoginPasswordApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val edLoginPasswordAlphaAnimator =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val signinButtonAlphaAnimator =
            ObjectAnimator.ofFloat(binding.signinButtonApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val orChooseAlphaAnimator =
            ObjectAnimator.ofFloat(binding.orChoose, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val haveAccountTextViewAlphaAnimator =
            ObjectAnimator.ofFloat(binding.haveAccountTextViewApp, View.ALPHA, 0f, 1f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val rotationAnimator =
            ObjectAnimator.ofFloat(binding.signinButtonApp, View.ROTATION, 0f, 360f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        val colorAnimator =
            ObjectAnimator.ofArgb(binding.signinButtonApp.background, "tint", Color.BLUE, Color.RED)
                .apply {
                    this.duration = duration
                    this.interpolator = interpolator
                }

        val translationYAnimator =
            ObjectAnimator.ofFloat(binding.signinButtonApp, View.TRANSLATION_Y, 0f, 100f).apply {
                this.duration = duration
                this.interpolator = interpolator
            }

        AnimatorSet().apply {
            playSequentially(
                ivSigninAlphaAnimator, tvLoginEmailAlphaAnimator, edLoginEmailAlphaAnimator,
                tvEdLoginPasswordAlphaAnimator, edLoginPasswordAlphaAnimator,
                signinButtonAlphaAnimator, orChooseAlphaAnimator, haveAccountTextViewAlphaAnimator,
                rotationAnimator, colorAnimator, translationYAnimator
            )
            this.startDelay = startDelay
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLoginApp.visibility = View.VISIBLE
            binding.progressBarLoginApp.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.progressBarLoginApp.visibility = View.VISIBLE
                    }
                })
        } else {
            binding.progressBarLoginApp.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.progressBarLoginApp.visibility = View.GONE
                    }
                })
            binding.progressBarLoginApp.visibility = View.GONE
        }
    }

    private fun animationAction() {
        binding.signinButtonApp.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            val duration = 500L
            val interpolator = AccelerateDecelerateInterpolator()

            val emailErrorAnimator = ObjectAnimator.ofFloat(
                binding.edLoginEmail,
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
                binding.edLoginPassword,
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
                playTogether(emailErrorAnimator, passwordErrorAnimator)
            }

            shakeAnimatorSet.start()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.user_input_name)
                }

                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.user_input_password)
                }

                password.length < 8 -> {
                    binding.edLoginPassword.error = getString(R.string.user_invalid_password)
                }

                else -> {
                    signinAppViewModel.signinPerson(email, password).observe(this) { result ->
                        val data = result.userLoginResult
                        if (data != null) {
                            signinAppViewModel.savePerson(
                                data.username,
                                data.userItemId,
                                data.usertoken
                            )
                            showSuccessDialog(result.appMessage)
                            if (result.appError) {
                                val errorMessage = when (result.appMessage) {
                                    "400" -> getString(R.string.user_invalid_email)
                                    "401" -> getString(R.string.user_not_found)
                                    else -> result.appMessage
                                }
                                showErrorDialog(errorMessage)
                            } else {
                                showSuccessDialog(result.appMessage)
                            }
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
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, 2000)
    }


}