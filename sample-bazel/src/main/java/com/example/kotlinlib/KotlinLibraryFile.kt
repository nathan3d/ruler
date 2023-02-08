package com.example.kotlinlib

import io.reactivex.rxjava3.core.*

data class KotlinLibraryFile(val someProp: String) {
    fun setup() {
        Flowable.just("Hello world").subscribe(System.out::println);

    }
}
