package com.hardhat.smishing

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform