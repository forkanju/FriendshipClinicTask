package com.compose.friendship.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.compose.friendship.Constants.Companion.ACTIVE
import com.compose.friendship.Constants.Companion.CREATE
import com.compose.friendship.Constants.Companion.INACTIVE
import com.compose.friendship.Constants.Companion.TYPE
import com.compose.friendship.Constants.Companion.UPDATE
import com.compose.friendship.Constants.Companion.USER
import com.compose.friendship.R
import com.compose.friendship.RequestState
import com.compose.friendship.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        setupObserver()
    }

    private fun setupUI() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.getUsers()
    }

    private fun setupListener() {
        binding.apply {
            btnActive.setOnClickListener {
                viewModel.changeButton(it.id)
            }
            btnDeactivate.setOnClickListener {
                viewModel.changeButton(it.id)
            }
            swipeRefresh.setOnRefreshListener {
                viewModel.getUsers()
            }
            btnCreate.setOnClickListener {
                findNavController().navigate(
                    R.id.action_navHome_to_navEdit,
                    bundleOf(TYPE to CREATE)
                )
            }
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            viewModel.getUserState.collect { data ->
                when (data) {
                    is RequestState.Error -> {
                        Timber.tag("HomeFragment").d("error: %s", data.error)
                        binding.swipeRefresh.isRefreshing = false
                    }

                    RequestState.Loading ->
                        binding.swipeRefresh.isRefreshing = true

                    is RequestState.Success ->
                        binding.swipeRefresh.isRefreshing = false
                }
            }
        }
        lifecycleScope.launch {
            viewModel.users.collect { users ->
                Timber.tag("HomeFragment").d("users: %s", users.size)
                binding.recyclerView.adapter = UserAdapter(
                    list = users,
                    onItemClicked = { findNavController() },
                    onEditClicked = {
                        findNavController().navigate(
                            R.id.action_navHome_to_navEdit,
                            bundleOf(TYPE to UPDATE, USER to it)
                        )
                    }
                )
            }
        }
        lifecycleScope.launch {
            viewModel.selectedButton.collect {
                binding.apply {
                    when (it) {
                        R.id.btnActive -> {
                            viewModel.filterUser(ACTIVE)

                            btnActive.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                            btnActive.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )

                            btnDeactivate.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            btnDeactivate.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                        }

                        R.id.btnDeactivate -> {
                            viewModel.filterUser(INACTIVE)

                            btnActive.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                            btnActive.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )

                            btnDeactivate.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                            btnDeactivate.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        }
                    }
                }
            }
        }
    }

}