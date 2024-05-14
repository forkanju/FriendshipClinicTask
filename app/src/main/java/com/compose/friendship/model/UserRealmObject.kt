package com.compose.friendship.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class UserRealmObject() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var email: String = ""
    var gender: String = ""
    var id: Int = 0
    var name: String = ""
    var status: String = ""

    constructor(id: Int, email: String, gender: String, name: String, status: String) : this() {
        _id.toHexString()
        this.id = id
        this.email = email
        this.gender = gender
        this.name = name
        this.status = status
    }

    constructor(email: String, gender: String, name: String, status: String) : this() {
        this.email = email
        this.gender = gender
        this.name = name
        this.status = status
    }

    fun toUserInfo() =
        UserInfo(id = id, name = name, email = email, status = status, gender = gender)

}