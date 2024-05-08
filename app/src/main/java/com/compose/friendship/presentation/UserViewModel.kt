package com.compose.friendship.presentation

import androidx.annotation.IdRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.friendship.R
import com.compose.friendship.RequestState
import com.compose.friendship.data.repo.UserRepo
import com.compose.friendship.model.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val selectedButton = savedStateHandle.getStateFlow("selectedButton", R.id.btnActive)

    private val _users = MutableStateFlow(listOf<UserInfo>())
    private val _usersCopy = MutableStateFlow(listOf<UserInfo>())
    val users = _users.asStateFlow()

    private val _getUserState = MutableSharedFlow<RequestState<List<UserInfo>>>()
    val getUserState = _getUserState.asSharedFlow()

    fun changeButton(@IdRes id: Int) {
        savedStateHandle["selectedButton"] = id
    }

    fun filterUser(status: String) {
        val filteredData = _usersCopy.value.filter { it.status == status }
        _users.update { filteredData }

    }

    fun getUsers() {
        viewModelScope.launch {
            _getUserState.emit(RequestState.Loading)
            val result = repo.getUsers()
            if (result is RequestState.Success) {
                _usersCopy.update { result.data }
                filterUser(if (selectedButton.value == R.id.btnActive) "active" else "inactive")
            }
            _getUserState.emit(result)
        }
    }

    fun createUser(
        name: String,
        email: String,
        gender: String,
        status: String,
        data: (RequestState<UserInfo>) -> Unit
    ) {

        viewModelScope.launch {
            val result = repo.create(name = name, email = email, gender = gender, status = status)
            data.invoke(result)
        }
    }

    fun updateUser(
        userId: String,
        name: String,
        email: String,
        gender: String,
        status: String,
        data: (RequestState<UserInfo>) -> Unit
    ) {


        viewModelScope.launch {
            val result = repo.update(
                userId = userId,
                name = name,
                email = email,
                gender = gender,
                status = status
            )
            data.invoke(result)


        }
    }


}