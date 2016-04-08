import org.junit.Assert
import org.junit.Test
import tatsuyuki.asynctask.Task
import tatsuyuki.asynctask.async
import tatsuyuki.asynctask.await

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
            var isGet = false
            val task = runOnAsync()
            task.result {
                data = it
                isGet = true
            }
            while(!isGet) {
                Thread.sleep(1)
            }
            Assert.assertNotNull(data)
            Assert.assertEquals("go", data)

        }
    }


    fun runOnAsync(): Task<String> = async() {
        Thread.sleep(100)
        "go"
    }
}
