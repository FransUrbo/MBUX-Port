package com.rndash.mbheadunit

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.rndash.mbheadunit.ui.ACDisplay
import com.rndash.mbheadunit.ui.ICDisplayTest
import com.rndash.mbheadunit.ui.MPGDisplay
import kotlin.math.abs
import kotlin.math.pow


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class FullscreenActivity : FragmentActivity() {
    private lateinit var viewPager: ViewPager2
    companion object {
        private lateinit var audiomanager: AudioManager
        var comm: CarComm? = null

        fun modifyVolume(increase: Boolean) {
            if (increase) {
                audiomanager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
            } else {
                audiomanager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        audiomanager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_fullscreen)
        viewPager = findViewById(R.id.ui_fragment)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.adapter = pagerAdapter
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }
    }

    override fun onResume() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        comm?.quit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        var x : UsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        var dev : UsbDevice? = null
        x.deviceList.forEach { (_, u) ->
            println("Found device V: ${u.vendorId} D: ${u.deviceId}")
            if (u.vendorId == 0x1a86) { // Arduino Uno
                val pi = PendingIntent.getBroadcast(
                    this, 0,
                    Intent("com.rnd-ash.github.mbui"), 0
                )
                dev = u
                x.requestPermission(dev, pi);
                return@forEach
            }
        }
        if (dev == null) {
            Log.e("MAIN", "No Arduino found!")
            Toast.makeText(this, "Error. Arduino not found!", Toast.LENGTH_LONG).show()
        } else {
            Log.d("MAIN", "Arduino found!")
            comm = CarComm(dev!!, x)
            Toast.makeText(this, "Connected to car!", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when(position) {
            1 -> ACDisplay()
            2 -> MPGDisplay()
            else -> ICDisplayTest()
        }
    }

    @RequiresApi(21)
    class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                when {
                    position < -1 -> {
                        alpha = 0f
                    }
                    position <= 1 -> {
                        translationX = 0.0f
                    }
                    else -> { alpha = 0f }
                }
            }
        }
    }
}
