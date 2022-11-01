package com.vishtech.todolist.mvvmtodo.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDatabaseTest {
    private lateinit var taskDao: TaskDao
    private lateinit var db: TaskDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TaskDatabase::class.java
        ).build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTask_returnTrue() = runBlocking {

        val task = Task(id = 1, name = "Sample Task")

        taskDao.insert(task)

        val taskList = taskDao.getTaskList()

        assertThat(taskList.contains(task)).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun updateTask_returnTrue() = runBlocking {

        val task = Task(id = 1, name = "Sample Task")

        taskDao.insert(task)

        task.completed = true
        taskDao.update(task)

        val taskList = taskDao.getTaskList()

        assertThat(taskList.contains(task)).isTrue()
    }

    @Test
    @Throws(Exception::class)
    fun deleteTask_returnFalse() = runBlocking {

        val testTask1 = Task(id = 1, name = "Sample Task 1")
        val testTask2 = Task(id = 2, name = "Sample Task 2")

        taskDao.insert(testTask1)
        taskDao.insert(testTask2)

        taskDao.delete(testTask1)

        val taskList = taskDao.getTaskList()

        assertThat(taskList.contains(testTask1)).isFalse()
    }

    @Test
    @Throws(Exception::class)
    fun deleteCompleteTask_returnTrue() = runBlocking {

        val testTask1 = Task(id = 1, name = "Sample Task 1", completed = true)
        val testTask2 = Task(id = 2, name = "Sample Task 2", completed = true)
        val testTask3 = Task(id = 2, name = "Sample Task 3", completed = false)

        taskDao.insert(testTask1)
        taskDao.insert(testTask2)
        taskDao.insert(testTask3)

        taskDao.deleteCompletedTasks()

        val taskList = taskDao.getTaskList()

        // it will delete only complete tasks
        assertThat(taskList.size).isEqualTo(1)
    }

}