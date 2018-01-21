package com.mueru.simplespotify

import io.github.konfigur8.ConfigurationTemplate
import io.github.konfigur8.Property
import java.util.*


object Settings {
    val CLIENT_ID = Property.string("CLIENT_ID")
    val REDIRECT_URI = Property.string("REDIRECT_URI")
    val REQUEST_CODE = Property.int("REQUEST_CODE")

    fun defaults() = ConfigurationTemplate()
            .withProp(CLIENT_ID, "c1fbf1a9074543cfbc84eb7c8515413c")
            .withProp(REDIRECT_URI, "simplespotify://callback")
            .withProp(REQUEST_CODE, Random().nextInt())
}