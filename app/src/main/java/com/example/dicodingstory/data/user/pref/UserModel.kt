package com.example.dicodingstory.data.user.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)