package com.warlike.androidhub

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HubSmokeTest {
    @Test fun startupAndBundledCatalog() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = context.assets.open("apps.json").bufferedReader().use { it.readText() }
        assertTrue(catalog.contains("warlike.android.hub.catalog.v1"))
        val packages = context.assets.open("android-apps.json").bufferedReader().use { it.readText() }
        assertTrue(AndroidPackages.fromJson(packages).isEmpty())
        val invalid = WarLikePackage("test", "Fake", "com.warlike.fake", 1,
            "https://malicious.example/installer.apk", "0".repeat(64))
        assertFalse(AndroidPackages.validate(invalid))
    }
}
