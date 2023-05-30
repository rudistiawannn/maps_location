package com.example.storiesapplication.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storiesapplication.R
import com.example.storiesapplication.databinding.ActivityLoginBinding
import com.example.storiesapplication.response.LoginResponse
import com.example.storiesapplication.ui.viewmodel.LoginViewModel
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.Constants
import com.example.storiesapplication.util.SingleEvent
import com.example.storiesapplication.util.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appPrefs = AppPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(
                this@LoginActivity,
                ViewModelFactory(appPrefs)
            )[LoginViewModel::class.java]

        binding.loginTvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginBtnLogin.setOnClickListener {
            if (!binding.loginEmailField.isError && !binding.loginPasswordField.isError) {
                loginViewModel.login(
                    binding.loginEmailField.text.toString(),
                    binding.loginPasswordField.text.toString()
                )
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.form_login_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        loginViewModel.getLoginData().observe(this@LoginActivity) { loginData: LoginResponse ->
            if (!(loginData.error as Boolean)) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.success_login),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        loginViewModel.getLoginError()
            .observe(this@LoginActivity) { loginError: SingleEvent<String> ->
                loginError.getData()?.let {
                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                }
            }

        loginViewModel.isLoading().observe(this@LoginActivity) { isLoading: Boolean ->
            binding.apply {
                loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                loginBtnLogin.isEnabled = !isLoading
                loginEmailField.isEnabled = !isLoading
                loginPasswordField.isEnabled = !isLoading
                loginTvRegister.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }
    }
}