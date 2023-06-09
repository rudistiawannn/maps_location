package com.example.storiesapplication.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storiesapplication.R
import com.example.storiesapplication.databinding.ActivityRegisterBinding
import com.example.storiesapplication.ui.viewmodel.RegisterViewModel
import com.example.storiesapplication.util.AppPreferences
import com.example.storiesapplication.util.Constants
import com.example.storiesapplication.util.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val Context.dataStore by preferencesDataStore(name = Constants.PREFS_NAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appPreferences = AppPreferences.getInstance(dataStore)
        val registerViewModel =
            ViewModelProvider(
                this@RegisterActivity,
                ViewModelFactory(appPreferences)
            )[RegisterViewModel::class.java]

        binding.registerBtnRegister.setOnClickListener {
            if (!binding.registerNameField.isError &&
                !binding.registerEmailField.isError &&
                !binding.registerPasswordField.isError
            ) {
                registerViewModel.register(
                    binding.registerNameField.text.toString(),
                    binding.registerEmailField.text.toString(),
                    binding.registerPasswordField.text.toString()
                )
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    getString(R.string.form_register_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.tvToLogin?.setOnClickListener {
            LoginActivity.start(this)
        }

        registerViewModel.getRegisterData().observe(this@RegisterActivity) { registerResponse ->
            if (!(registerResponse.error as Boolean)) {
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

        registerViewModel.getRegisterError().observe(this@RegisterActivity) { registerError ->
            registerError.getData()?.let {
                Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.isLoading().observe(this@RegisterActivity) { isLoading: Boolean ->
            binding.registerProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.registerBtnRegister.isEnabled = !isLoading
            binding.registerNameField.isEnabled = !isLoading
            binding.registerEmailField.isEnabled = !isLoading
            binding.registerPasswordField.isEnabled = !isLoading
        }
    }
}