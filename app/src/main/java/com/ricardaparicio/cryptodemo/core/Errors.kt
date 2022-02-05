package com.ricardaparicio.cryptodemo.core

sealed interface Error
sealed interface FeatureError: Error

object NetworkError : Error
object ServerError : Error