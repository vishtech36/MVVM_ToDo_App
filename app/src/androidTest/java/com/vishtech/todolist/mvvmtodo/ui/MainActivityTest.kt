package com.vishtech.todolist.mvvmtodo.ui

import android.view.View
import android.widget.CheckBox
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.vishtech.todolist.R
import com.vishtech.todolist.mvvmtodo.ui.tasks.TasksAdapter
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {


    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = launchActivity()
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test

    fun addNewTask() {
        val task = "Sample Task"
        onView(withId(R.id.fab_add_task)).perform(click())
        onView(withId(R.id.edit_text_task_name)).perform(ViewActions.typeText(task))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.fab_save_task)).perform(click())
        onView(withText(task)).check(matches(isDisplayed()))
    }

    @Test
    fun addNewTaskWithImportance() {
        val task = "Sample Task with Importance"
        onView(withId(R.id.fab_add_task)).perform(click())
        onView(withId(R.id.edit_text_task_name)).perform(ViewActions.typeText(task))
        onView(withId(R.id.check_box_important)).perform(click())
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.fab_save_task)).perform(click())
        onView(withText(task)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteAllCompleteTask() {
        val task = "Sample Task with Importance"
        onView(withId(R.id.fab_add_task)).perform(click())
        onView(withId(R.id.edit_text_task_name)).perform(ViewActions.typeText(task))
        onView(withId(R.id.check_box_important)).perform(click())
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.fab_save_task)).perform(click())
        val count = getCountFromRecyclerView(R.id.recycler_view_tasks)

        (0 until count).forEach { item ->
            onView(withId(R.id.recycler_view_tasks)).perform(
                RecyclerViewActions.actionOnItemAtPosition<TasksAdapter.TasksViewHolder>(
                    item,
                    clickOnViewChild(R.id.check_box_completed)
                )
            )
        }
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext);

        onView(withText("Delete all completed")).perform(click())
        onView(withText("YES")).perform(click())
        assertEquals(getCountFromRecyclerView(R.id.recycler_view_tasks), 0)

    }
}

fun clickOnViewChild(viewId: Int) = object : ViewAction {
    override fun getConstraints() = null

    override fun getDescription() = "Click on a child view with specified id."

    override fun perform(uiController: UiController, view: View) {
        if(!view.findViewById<CheckBox>(R.id.check_box_completed).isChecked)
            click().perform(uiController, view.findViewById<View>(viewId))
    }
}

fun getCountFromRecyclerView(@IdRes RecyclerViewId: Int): Int {
    var count = 0
    val matcher = object : TypeSafeMatcher<View?>() {
        override fun matchesSafely(item: View?): Boolean {
            count = (item as RecyclerView).adapter!!.itemCount
            return true
        }

        override fun describeTo(description: Description?) {

        }

    }
    onView(allOf(withId(RecyclerViewId), isDisplayed())).check(matches(matcher))
    val result = count
    count = 0
    return result
}