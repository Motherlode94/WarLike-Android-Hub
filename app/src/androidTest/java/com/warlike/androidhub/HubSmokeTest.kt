package com.warlike.androidhub

import androidx.test.ext.junit.runners.AndroidJUnit4
import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HubSmokeTest {
    @Test fun activityLaunchesOnAndroid() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val intent = android.content.Intent(instrumentation.targetContext, MainActivity::class.java)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = instrumentation.startActivitySync(intent)
        try { assertTrue(activity is MainActivity) }
        finally { activity.finish() }
    }
    @Test fun startupAndBundledCatalog() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = context.assets.open("apps.json").bufferedReader().use { it.readText() }
        assertTrue(catalog.contains("warlike.android.hub.catalog.v1"))
        val packages = context.assets.open("android-apps.json").bufferedReader().use { it.readText() }
        assertTrue(AndroidPackages.fromJson(packages).isEmpty())
        val invalid = WarLikePackage("test", "Fake", "com.warlike.fake", 1,
            "https://malicious.example/installer.apk", "0".repeat(64))
        assertFalse(AndroidPackages.validate(invalid))
        val valid = WarLikePackage("future-soldier", "WarLike Soldier",
            "com.warlike.soldier", 10,
            "https://github.com/Motherlode94/WarLike-Soldier/releases/download/v1.0.0/warlike-soldier.apk",
            "a".repeat(64))
        assertTrue(AndroidPackages.validate(valid))
        assertFalse(AndroidPackages.validate(valid.copy(sha256 = "invalid")))
        assertFalse(AndroidPackages.validate(valid.copy(packageName = "com.other.app")))
        assertFalse(AndroidPackages.validate(valid.copy(downloadUrl = "https://github.com/outsider/fake/releases/download/v1/test.apk")))
    }
    @Test fun favoriteSurvivesClosingAndReopening() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val preferences = context.getSharedPreferences("warlike.androidhub.v1", android.content.Context.MODE_PRIVATE)
        val oldValue = preferences.getBoolean("favorite:engine", false)
        preferences.edit().putBoolean("favorite:engine", false).commit()
        fun start(): Activity {
            val intent = android.content.Intent(context, MainActivity::class.java)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            return instrumentation.startActivitySync(intent)
        }
        fun find(view: android.view.View, description: String): android.view.View? {
            if (view.contentDescription?.toString() == description) return view
            if (view is android.view.ViewGroup) {
                for (index in 0 until view.childCount) {
                    val result = find(view.getChildAt(index), description)
                    if (result != null) return result
                }
            }
            return null
        }
        var activity: Activity? = null
        try {
            val first = start()
            activity = first
            instrumentation.runOnMainSync {
                val star = find(first.window.decorView, "Ajouter WarLike Engine aux favoris")
                assertNotNull("Bouton de favori introuvable", star)
                star!!.performClick()
            }
            instrumentation.waitForIdleSync()
            assertTrue(preferences.getBoolean("favorite:engine", false))
            instrumentation.runOnMainSync { first.finish() }
            instrumentation.waitForIdleSync()
            val second = start()
            activity = second
            instrumentation.runOnMainSync {
                assertNotNull("Favori perdu après réouverture",
                    find(second.window.decorView, "Retirer WarLike Engine des favoris"))
            }
        } finally {
            activity?.let { current -> instrumentation.runOnMainSync { current.finish() } }
            preferences.edit().putBoolean("favorite:engine", oldValue).commit()
        }
    }

}
