package com.compose.friendship.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.compose.friendship.Constants.Companion.ACTIVE
import com.compose.friendship.Constants.Companion.CREATE
import com.compose.friendship.Constants.Companion.INACTIVE
import com.compose.friendship.Constants.Companion.TYPE
import com.compose.friendship.Constants.Companion.UPDATE
import com.compose.friendship.Constants.Companion.USER
import com.compose.friendship.R
import com.compose.friendship.RequestState
import com.compose.friendship.databinding.FragmentEditBinding
import com.compose.friendship.model.User
import com.google.android.material.snackbar.Snackbar

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private val viewModel by activityViewModels<UserViewModel>()
    private var selectedGender = ""
    private var type: String? = null
    private var user: User.UserInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        arguments?.let {
            type = it.getString(TYPE)
            @Suppress("DEPRECATION")
            user = it.getParcelable(USER)

        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListener()

    }

    private fun setupListener() {
        binding.apply {
            // Handle back button clicks
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            btnCreateOrUpdate.setOnClickListener {
                val name = name.text.toString()
                val email = email.text.toString()
                // Validate the input fields
                if (name.isBlank() || email.isBlank()) {
                    Snackbar.make(
                        requireView(),
                        "Name and email are required",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else if (selectedGender.isBlank()) {
                    Snackbar.make(
                        requireView(),
                        "Select gender!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (type == CREATE) {
                    viewModel.createUser(
                        name = name,
                        email = email,
                        gender = selectedGender,
                        status = if (status.isChecked) ACTIVE else INACTIVE,
                    ) { result ->
                        when (result) {
                            is RequestState.Error ->
                                view?.let { it2 ->
                                    Snackbar.make(
                                        it2,
                                        "Something went wrong!",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }

                            RequestState.Loading -> {}
                            is RequestState.Success ->
                                view?.let { it2 ->
                                    Snackbar.make(
                                        it2,
                                        "User Created Successfully!",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                } else {
                    user?.id?.let { it1 ->
                        viewModel.updateUser(
                            userId = it1.toString(),
                            name = name,
                            email = email,
                            gender = selectedGender,
                            status = if (status.isChecked) ACTIVE else INACTIVE,
                        ) { result ->
                            when (result) {
                                is RequestState.Error ->
                                    view?.let { it2 ->
                                        Snackbar.make(
                                            it2,
                                            "Something went wrong!",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }

                                RequestState.Loading -> {}
                                is RequestState.Success ->
                                    view?.let { it2 ->
                                        Snackbar.make(
                                            it2,
                                            "User Updated Successfully!",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        if (type == UPDATE) {
            binding.topAppBar.title = UPDATE
            binding.btnCreateOrUpdate.text = getString(R.string.update)

            binding.apply {
                name.setText(user?.name)
                email.setText(user?.email)
                selectedGender = user?.gender.toString()
                gender.setText(user?.gender)
                binding.status.isChecked =
                    user?.status == ACTIVE
            }
        }


        val items = listOf("male", "female")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        binding.gender.setAdapter(adapter)
        binding.gender.setOnItemClickListener { _, _, position, _ ->
            selectedGender = items[position]
        }
    }

}
