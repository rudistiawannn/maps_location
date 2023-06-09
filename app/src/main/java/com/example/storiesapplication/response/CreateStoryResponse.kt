package com.example.storiesapplication.response

import com.google.gson.annotations.SerializedName

data class CreateStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
