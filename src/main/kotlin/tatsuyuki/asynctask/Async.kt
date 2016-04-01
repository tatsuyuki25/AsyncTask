package tatsuyuki.asynctask

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

/**
 *
 * Created by tatsuyuki on 2016/4/1.
 */


class Task<T> {
    internal  val LOCK: Lock = ReentrantLock()
    internal  var isLocked: Boolean = false
    var result: ((T) -> Unit) = {}
    fun result(result: ((T) -> Unit)) {
        this.result = result
    }
}
fun <T> async(body: () -> T): Task<T> {
    val task: Task<T> = Task()
    thread {
        task.LOCK.lock()
        task.isLocked = true
        try {
            var data = body()
            task.result(data)
        } finally {
            task.LOCK.unlock()
        }
    }
    return task
}

fun <T> await(body: () -> T): Any? {
    var task = body()
    var result: Any? = null
    if(task is Task<*>) {
        task.result {
            result = it
        }
        while(!task.isLocked) {
            Thread.sleep(1)
        }
        task.LOCK.lock()
        task.LOCK.unlock()
    }
    return result
}


