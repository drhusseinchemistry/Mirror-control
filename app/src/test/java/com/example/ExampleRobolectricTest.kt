package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EShare TV Remote", appName)
  }

  @Test
  fun `test default channels exist`() {
    val channels = com.example.model.TvAppState.DEFAULT_CHANNELS
    assertTrue(channels.isNotEmpty())
    assertEquals("Kurdistan TV HD", channels[0].name)
  }
}
