
import org.junit.Assert
import org.junit.Test
import tatsuyuki.asynctask.Task
import tatsuyuki.asynctask.async
import tatsuyuki.asynctask.await
import kotlin.system.measureTimeMillis

/**
 * by test
 * Created by tatsuyuki on 2016/4/7.
 */
class TestAsync {

    @Test
    fun awaitNullTest() {
        for(i in 1..100) {
            val s = await { runOnAsync() }
            Assert.assertNotNull(s)
            Assert.assertEquals("go", s)
        }
    }

    @Test
    fun asyncTest() {
        for(i in 1..100) {
            var data: String? = null
            var isGetData = false
            val task = runOnAsync()
            task.result {
                data = it
                isGetData = true
            }
            while(!isGetData) {
                Thread.sleep(1)
            }
            Assert.assertNotNull(data)
            Assert.assertEquals("go", data)

        }
    }

    @Test
    fun awaitTest() {
        val time = measureTimeMillis {
            val data = await { runOnAsync() }
            Assert.assertNotNull(data)
            Assert.assertEquals("go", data)
        }
        Assert.assertTrue(time >= 100)
    }

    @Test
    fun asyncResultTest() {
        val task = runOnAsync()
        Thread.sleep(200)
        task.result { Assert.assertEquals("go", it) }
    }

    fun runOnAsync(): Task<String> = async() {
        Thread.sleep(100)

        return@async "go"
    }

    @Test
    fun asyncExceptionTest() {
        val task = runAsyncException()
        Thread.sleep(10)
        task.result { Assert.assertNull(it) }
    }

    @Test
    fun awaitExceptionTest() {
        try {
            await { runAsyncException() }
        } catch(e: Exception) {
            Assert.assertEquals("java.lang.RuntimeException: async error", e.message)
        }
    }

    fun runAsyncException(): Task<String> = async() {
        throw RuntimeException("async error")

        return@async  ""
    }
}
