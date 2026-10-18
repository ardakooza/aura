package com.example.data.model

enum class UserGender(val label: String) {
    MALE("Erkek"),
    FEMALE("Kadın"),
    UNISEX("Tümü / Unisex")
}

enum class AgeBracket(val label: String) {
    AGE_18_24("18 – 24"),
    AGE_25_34("25 – 34"),
    AGE_35_44("35 – 44"),
    AGE_45_PLUS("45+")
}

enum class StyleVibe(val label: String, val tag: String) {
    MINIMALIST("Minimalist & Sade", "Minimalist"),
    OLD_MONEY("Old Money & Lüks", "Luxury"),
    STREETWEAR("Streetwear & Kentsel", "Street"),
    CYBER("Cyber & Fütüristik", "Cyber"),
    VINTAGE("Vintage & Retro", "Retro")
}

data class UserProfile(
    val gender: UserGender = UserGender.UNISEX,
    val ageBracket: AgeBracket = AgeBracket.AGE_25_34,
    val preferredStyles: Set<StyleVibe> = setOf(StyleVibe.MINIMALIST),
    val isOnboarded: Boolean = false
)
