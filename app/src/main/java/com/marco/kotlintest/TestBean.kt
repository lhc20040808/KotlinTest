package com.marco.kotlintest

data class TestBean(val info: String = "default" , val test : String) {

    override fun toString(): String {
        return "TestBean(info='$info', test='$test')"
    }
}