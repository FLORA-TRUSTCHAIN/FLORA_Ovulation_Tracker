package com.example.flora1.core.network

interface FloraApi : Api {
    override val baseUrl: String
        get() = "http://spartacus.ee.duth.gr:7093"
}
