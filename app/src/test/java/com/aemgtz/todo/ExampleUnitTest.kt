package com.aemgtz.todo

import android.util.Log
import com.aemgtz.todo.data.Task
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun createMockupTasks(){
        val tasks = mutableListOf<Task>()
        for (i in 0..9) {
            tasks.add(Task(i+1, "Task ${i + 1}", "Detail ${i + 1}", false))
        }
        assertEquals(10, tasks.size)
    }
}