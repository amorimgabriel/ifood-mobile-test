package br.com.tweetanalyzer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by gabrielsamorim
 * on 19/05/18.
 */
data class Document(@Expose @SerializedName("content") val text: String,
                    @Expose @SerializedName("type") val textType: String = "PLAIN-TEXT")