package tatsuyuki.asynctask

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

/**
 * C# style async & await
 * @author tatsuyuki
 * @since 1.1.6
 * @see async
 * @see await
 */
class Task<T> {

    internal val LOCK: Lock = ReentrantLock()
    internal var result: ((T?) -> Unit)? = null
    internal var e: Exception? = null
    internal var data: T? = null
    internal val isSendResult: AtomicBoolean = AtomicBoolean(false)
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
}

/**
 * run method on async
 * @param body method body
 * @return Task<T>, using task.result{} set callback
 * @see await
 */
fun <T> async(body: () -> T): Task<T> {
    val task: Task<T> = Task()
    thread {
        task.LOCK.lock()
        try {
            task.data = body()
        } catch(e: Exception) {
            task.e = e
        } finally {
            task.LOCK.unlock()
        }
        if (!task.isSendResult.get() && task.result != null) {
            task.isSendResult.set(true)
            task.result!!(task.data)
        }

    }
    return task
}

/**
 * Async method to block
 * @param body async method
 * @return async method return value, return on call await thread
 */
fun <T> await(body: () -> Task<T>): T? {
    var task = body()
    var result: T? = null
    var isGetResult = false
    task.result {
        result = it
        isGetResult = true
    }
    while (!isGetResult) {
        Thread.sleep(1)
    }
    task.LOCK.lock()
    task.LOCK.unlock()
    if (task.e != null) {
        throw Exception(task.e)
    }
    return result
}


