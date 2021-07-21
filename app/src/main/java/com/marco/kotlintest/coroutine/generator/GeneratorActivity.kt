package com.marco.kotlintest.coroutine.generator

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.marco.kotlintest.R
import java.lang.IllegalStateException
import kotlin.coroutines.*

class GeneratorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
    }

    sealed class State {
        class NotReady(val continuation: Continuation<Unit>) : State()
        class Ready<T>(val continuation: Continuation<Unit>, val nextValue: T) : State()
        object Done : State()
    }

    interface Generator<T> {
        operator fun iterator(): Iterator<T>
    }

    class GeneratorImpl<T>(
        private val block: suspend GeneratorScope<T>.(T) -> Unit,
        private val parameter: T
    ) : Generator<T> {
        override fun iterator(): Iterator<T> {
            return GeneratorIterator(block, parameter)
        }
    }

    class GeneratorIterator<T>(
        private val block: suspend GeneratorScope<T>.(T) -> Unit,
        override val parameter: T
    ) : GeneratorScope<T>(), Iterator<T>, Continuation<Any?> {

        private var state: State

        init {
            val coroutineBlock: suspend GeneratorScope<T>.() -> Unit = {
                block(parameter)
            }
            val start = coroutineBlock.createCoroutine(this, this)
            state = State.NotReady(start)
        }

        override val context: CoroutineContext = EmptyCoroutineContext

        override fun hasNext(): Boolean {
            resume()
            return state != State.Done
        }

        private fun resume() {
            when (val curState = state) {
                is State.NotReady -> curState.continuation.resume(Unit)
            }
        }

        override fun next(): T {
            Log.d("lhc", "next")
            return when (val curState = state) {
                is State.NotReady -> {
                    resume()
                    return next()
                }
                is State.Ready<*> -> {
                    state = State.NotReady(curState.continuation)
                    (curState as State.Ready<T>).nextValue
                }
                State.Done -> throw IllegalStateException()
            }
        }


        override fun resumeWith(result: Result<Any?>) {
            Log.d("lhc", "resumeWith")
            state = State.Done
            result.getOrThrow()
        }

        override suspend fun yield(value: T) = suspendCoroutine<Unit> { continuation ->
            state = when (state) {
                is State.NotReady -> State.Ready(continuation, value)
                is State.Ready<*> -> throw IllegalStateException("cannot yield a value while ready.")
                State.Done -> throw IllegalStateException("cannot yield a value while done.")
            }
        }

    }

    abstract class GeneratorScope<T> internal constructor() {

        protected abstract val parameter: T

        abstract suspend fun yield(value: T)
    }

    fun <T> generator(block: suspend GeneratorScope<T>.(T) -> Unit): (T) -> Generator<T> {
        return { parameter ->
            GeneratorImpl(block, parameter)
        }
    }

    fun test() {
        val numGenerator = generator { start: Int ->
            for (i in 0..5) {
                yield(start + i)
            }
        }
        val seq = numGenerator(10)
    }
}