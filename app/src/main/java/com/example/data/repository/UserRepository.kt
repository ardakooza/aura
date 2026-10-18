package com.example.data.repository

import com.example.data.local.UserProfileDao
import com.example.data.local.UserProfileEntity
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val userProfileDao: UserProfileDao) {

    val userProfile: Flow<UserProfile?> = userProfileDao.getProfile().map { entity ->
        entity?.toDomain()
    }

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.saveProfile(UserProfileEntity.fromDomain(profile))
    }
}
