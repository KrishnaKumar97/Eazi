package com.nineleaps.eazipoc

interface ObjectCallback<T> {
    fun onSuccess(value: T)
    fun onFailure(e: Exception)
}