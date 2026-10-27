// SMCPKG_SUPPORT>>>Cursor048
package com.apsmkimo.tetravideoplayer.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.apsmkimo.tetravideoplayer.R

/**
 * Per-app language. [SYSTEM] clears the override so the phone locale is used,
 * falling back to English when that locale is not one of the supported five.
 */
enum class AppLanguage(
    val languageTag: String?,
    val labelRes: Int,
) {
    SYSTEM(languageTag = null, labelRes = R.string.settings_language_system),
    ENGLISH(languageTag = "en", labelRes = R.string.language_name_en),
    TRADITIONAL_CHINESE(languageTag = "zh-Hant", labelRes = R.string.language_name_zh_hant),
    SIMPLIFIED_CHINESE(languageTag = "zh-Hans", labelRes = R.string.language_name_zh_hans),
    JAPANESE(languageTag = "ja", labelRes = R.string.language_name_ja),
    SPANISH(languageTag = "es", labelRes = R.string.language_name_es),
    ;

    fun apply() {
        val locales = if (languageTag == null) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    companion object {
        fun current(): AppLanguage {
            val tags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            if (tags.isEmpty()) return SYSTEM
            val first = tags.substringBefore(',')
            return fromTag(first)
        }

        private fun fromTag(tag: String): AppLanguage {
            val normalized = tag.replace('_', '-')
            return when {
                normalized.equals("zh-Hant", ignoreCase = true) ||
                    normalized.startsWith("zh-Hant-", ignoreCase = true) ||
                    normalized.equals("zh-TW", ignoreCase = true) ||
                    normalized.equals("zh-HK", ignoreCase = true) ||
                    normalized.equals("zh-MO", ignoreCase = true) -> TRADITIONAL_CHINESE
                normalized.startsWith("zh-Hans", ignoreCase = true) ||
                    normalized.equals("zh-CN", ignoreCase = true) ||
                    normalized.equals("zh-SG", ignoreCase = true) -> SIMPLIFIED_CHINESE
                normalized.startsWith("zh", ignoreCase = true) &&
                    normalized.contains("Hant", ignoreCase = true) -> TRADITIONAL_CHINESE
                normalized.startsWith("zh", ignoreCase = true) -> SIMPLIFIED_CHINESE
                normalized.startsWith("ja", ignoreCase = true) -> JAPANESE
                normalized.startsWith("es", ignoreCase = true) -> SPANISH
                normalized.startsWith("en", ignoreCase = true) -> ENGLISH
                else -> SYSTEM
            }
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor048
