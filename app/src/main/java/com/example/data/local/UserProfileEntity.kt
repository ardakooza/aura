package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AgeBracket
import com.example.data.model.StyleVibe
import com.example.data.model.UserGender
import com.example.data.model.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val gender: String,
    val ageBracket: String,
    val preferredStyles: String,
    val isOnboarded: Boolean
) {
    fun toDomain(): UserProfile {
        val userGender = try { UserGender.valueOf(gender) } catch (e: Exception) { UserGender.UNISEX }
        val age = try { AgeBracket.valueOf(ageBracket) } catch (e: Exception) { AgeBracket.AGE_25_34 }
        val styles = preferredStyles.split(",")
            .mapNotNull {
                val clean = it.trim()
                if (clean.isNotEmpty()) {
                    try { StyleVibe.valueOf(clean) } catch (e: Exception) { null }
                } else null
            }
            .toSet()
            .ifEmpty { setOf(StyleVibe.MINIMALIST) }

        return UserProfile(
            gender = userGender,
            ageBracket = age,
            preferredStyles = styles,
            isOnboarded = isOnboarded
        )
    }

    companion object {
        fun fromDomain(profile: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                id = 1,
                gender = profile.gender.name,
                ageBracket = profile.ageBracket.name,
                preferredStyles = profile.preferredStyles.joinToString(",") { it.name },
                isOnboarded = profile.isOnboarded
            )
        }
    }
}
