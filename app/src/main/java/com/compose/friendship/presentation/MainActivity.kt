package com.compose.friendship.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.compose.friendship.connectivity.ConnectivityObserver
import com.compose.friendship.connectivity.NetworkConnectivityObserver
import com.compose.friendship.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            NetworkConnectivityObserver(this@MainActivity).observe().collect {
                Log.d("TAG", "onCreate: $it")
                when (it) {
                    ConnectivityObserver.Status.Available -> {
                        viewModel.isConnected = true
                    }

                    ConnectivityObserver.Status.Unavailable -> {
                        viewModel.isConnected = false
                        Snackbar.make(binding.root, "No Connection", Snackbar.LENGTH_SHORT).show()
                    }

                    ConnectivityObserver.Status.Losing -> Snackbar.make(
                        binding.root, "Poor Connection!", Snackbar.LENGTH_SHORT
                    ).show()

                    ConnectivityObserver.Status.Lost -> {
                        viewModel.isConnected = false
                        Snackbar.make(binding.root, "Connection Lost", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}