package tatsuyuki.asynctask

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

/**
 * C# style async & await
 * @author tatsuyuki
 * @since 1.2.1
 * @see async
 * @see await
 */
class Task<T> {

    private val LOCK: Lock = ReentrantLock()
    private var result: ((T?) -> Unit)? = null
    private var e: Exception? = null
    private var data: T? = null
    private val isSendResult: AtomicBoolean = AtomicBoolean(false)

    /**
     * Set callback, run callback on new thread
     * @param result the callback
     */
    fun result(result: ((T?) -> Unit)) {
        this.result = result
        if ((e != null || data != null) && !isSendResult.get()) {
            isSendResult.set(true)
            thread {
                result(data)
            }
        }
    }

    /**
     * run method on async
     * @param body method body
     * @return Task<T>, using task.result{} set callback
     * @see await
     */
    fun async(body: () -> T): Task<T> {
        thread {
            LOCK.lock()
            try {
                data = body()
            } catch(e: Exception) {
                this.e = e
            } finally {
                LOCK.unlock()
            }
            if (!isSendResult.get() && result != null) {
                isSendResult.set(true)
                result!!(data)
            }

        }
        return this
    }

    /**
     * Async method to block
     * @return async method return value, return on call await thread
     * @see async
     */
    fun await(): T? {
        var result: T? = null
        var isGetResult = false
        this.result {
            result = it
            isGetResult = true
        }
        while (!isGetResult) {
            Thread.sleep(1)
        }
        this.LOCK.lock()
        this.LOCK.unlock()
        if (this.e != null) {
            throw Exception(this.e)
        }
        return result
    }
}

/**
 * run method on async
 * @param body method body
 * @return Task<T>, using task.result{} set callback
 * @see await
 */
fun <T> async(body: () -> T): Task<T> {
    val task: Task<T> = Task()
    return task.async(body)
}

/**
 * Async method to block
 * @param body async method
 * @return async method return value, return on call await thread
 * @see async
 */
fun <T> await(body: () -> Task<T>): T? {
    var task = body()
    return task.await()
}


