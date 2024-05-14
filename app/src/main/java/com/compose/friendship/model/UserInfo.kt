package com.compose.friendship.model


import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    @JsonProperty("email")
    var email: String = "",
    @JsonProperty("gender")
    var gender: String = "",
    @JsonProperty("id")
    val id: Int = 0,
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("status")
    var status: String = ""
) : Parcelable{
    fun toUserRealmObject() = UserRealmObject(id = id, name = name, email = email, status = status, gender = gender)
}
