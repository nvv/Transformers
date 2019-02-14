package com.vnamashko.transformers.network.model

import com.google.gson.annotations.SerializedName

/**
 * @author Vlad Namashko
 */
data class Transformers(
        @SerializedName("transformers") val transformers: List<Transformer>
)