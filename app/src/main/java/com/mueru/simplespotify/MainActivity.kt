package com.mueru.simplespotify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.mueru.simplespotify.Settings.CLIENT_ID
import com.mueru.simplespotify.Settings.REDIRECT_URI
import com.mueru.simplespotify.Settings.REQUEST_CODE
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.player.*

private val config = Settings.defaults().reify()

class MainActivity : Activity(), Player.NotificationCallback, ConnectionStateCallback {

    private lateinit var mPlayer: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authRequestBuilder = AuthenticationRequest.Builder(
                config[CLIENT_ID],
                AuthenticationResponse.Type.TOKEN,
                config[REDIRECT_URI])

        authRequestBuilder.setScopes(arrayOf("user-read-private", "streaming"))
        val request = authRequestBuilder.build()
        AuthenticationClient.openLoginActivity(this, config[REQUEST_CODE], request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == config[REQUEST_CODE]) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            if (response.type == AuthenticationResponse.Type.TOKEN) {
                val playerConfig = Config(this, response.accessToken, config[CLIENT_ID])
                Spotify.getPlayer(playerConfig, this, object : SpotifyPlayer.InitializationObserver {
                    override fun onInitialized(spotifyPlayer: SpotifyPlayer) {
                        mPlayer = spotifyPlayer
                        mPlayer.addConnectionStateCallback(this@MainActivity)
                        mPlayer.addNotificationCallback(this@MainActivity)
                    }

                    override fun onError(throwable: Throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.message)
                    }
                })
            }
        }
    }

    override fun onDestroy() {
        Spotify.destroyPlayer(this)
        super.onDestroy()
    }

    override fun onPlaybackError(error: Error) {
        Log.d("MainActivity", "Playback error received: " + error.name)
    }

    override fun onPlaybackEvent(playerEvent: PlayerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name)
    }

    override fun onLoggedOut() {
        Log.d("MainActivity", "User logged out")
    }

    override fun onLoggedIn() {
        Log.d("MainActivity", "User logged in")

        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0)
    }

    override fun onConnectionMessage(message: String) {
        Log.d("MainActivity", "Received connection message: " + message)
    }

    override fun onLoginFailed(error: Error) {
        Log.d("MainActivity", "Login failed received: " + error.name)
    }

    override fun onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred")
    }
}
