package ir.danialchoopan.medimate.util

import ir.danialchoopan.medimate.domain.model.IntervalType
import ir.danialchoopan.medimate.domain.model.Reminder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ReminderSchedulerTest {

    private fun createReminder(
        intervalType: IntervalType,
        intervalValue: Int = 1,
        cycleOnDays: Int = 0,
        cycleOffDays: Int = 0,
        nextReminderTime: Long = System.currentTimeMillis()
    ) = Reminder(
        id = 1,
        medicineId = 1,
        intervalType = intervalType,
        intervalValue = intervalValue,
        nextReminderTime = nextReminderTime,
        cycleOnDays = cycleOnDays,
        cycleOffDays = cycleOffDays
    )

    @Test
    fun `calculateNextReminderTime MINUTES adds correct interval`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.MINUTES, intervalValue = 5)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)
        assertEquals(lastTime + 5 * 60 * 1000L, result)
    }

    @Test
    fun `calculateNextReminderTime HOURS adds correct interval`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.HOURS, intervalValue = 2)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)
        assertEquals(lastTime + 2 * 60 * 60 * 1000L, result)
    }

    @Test
    fun `calculateNextReminderTime DAYS adds correct interval`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.DAYS, intervalValue = 3)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)
        assertEquals(lastTime + 3 * 24 * 60 * 60 * 1000L, result)
    }

    @Test
    fun `calculateNextReminderTime WEEKS adds correct interval`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.WEEKS, intervalValue = 1)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)
        assertEquals(lastTime + 7 * 24 * 60 * 60 * 1000L, result)
    }

    @Test
    fun `calculateNextReminderTime EVEN_DAYS skips to next even day`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.EVEN_DAYS)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)

        val nextDate = Instant.ofEpochMilli(result).atZone(ZoneOffset.UTC)
        assertTrue("Next day should be even", nextDate.dayOfMonth % 2 == 0)
        assertTrue("Next day should be after last time", result > lastTime)
    }

    @Test
    fun `calculateNextReminderTime ODD_DAYS skips to next odd day`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.ODD_DAYS)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)

        val nextDate = Instant.ofEpochMilli(result).atZone(ZoneOffset.UTC)
        assertTrue("Next day should be odd", nextDate.dayOfMonth % 2 == 1)
        assertTrue("Next day should be after last time", result > lastTime)
    }

    @Test
    fun `calculateNextReminderTime CYCLE respects on/off pattern`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.CYCLE, cycleOnDays = 5, cycleOffDays = 2)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)

        assertTrue("Next time should be after last time", result > lastTime)
    }

    @Test
    fun `calculateNextReminderTime CYCLE with zero cycle falls back to daily`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.CYCLE, cycleOnDays = 0, cycleOffDays = 0)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)
        assertEquals(lastTime + 24 * 60 * 60 * 1000L, result)
    }

    @Test
    fun `calculateNextReminderTime uses UTC timestamps`() {
        val lastTime = 1700000000000L
        val reminder = createReminder(IntervalType.DAYS, intervalValue = 1)
        val result = ReminderScheduler.calculateNextReminderTime(reminder, lastTime)

        val zonedDateTime = Instant.ofEpochMilli(result).atZone(ZoneOffset.UTC)
        assertEquals(ZoneOffset.UTC, zonedDateTime.offset)
    }
}
