package com.example.pomodojo

import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateAdviceTest {

    @Test
    fun `test advice when no weekly Pomodoros are done`() {
        val advice = generateAdvice(
            totalPomodoros = 50,
            totalLongExercises = 10,
            totalShortExercises = 5,
            weeklyPomodoros = 0,
            weeklyLongExercises = 2,
            weeklyShortExercises = 3
        )
        assertTrue(advice.size == 1)
        assertTrue(advice.contains("It looks like you haven't done any Pomodoros this week. Try to get back on track!"))
    }

    @Test
    fun `test advice when weekly Pomodoros are less than 10`() {
        val advice = generateAdvice(
            totalPomodoros = 60,
            totalLongExercises = 15,
            totalShortExercises = 8,
            weeklyPomodoros = 5,
            weeklyLongExercises = 2,
            weeklyShortExercises = 3
        )

        assertTrue(advice.contains("Try to do more Pomodoros to boost your productivity. Aim for at least 10 sessions per week."))
        assertTrue(advice.contains("You've completed over 50 Pomodoros. Keep pushing toward your goals!"))
        assertTrue(advice.contains("Consider doing more long breathing exercises to help you relax and recharge."))
        assertTrue(advice.contains("Try to incorporate more short breathing exercises into your routine to reduce daily stress."))
        assertTrue(advice.contains("You're maintaining a good balance between work and relaxation. Keep it up!"))

        assertTrue(advice.size == 5)
    }

    @Test
    fun `test advice when weekly Pomodoros are between 10 and 20`() {
        val advice = generateAdvice(
            totalPomodoros = 120,
            totalLongExercises = 20,
            totalShortExercises = 10,
            weeklyPomodoros = 15,
            weeklyLongExercises = 4,
            weeklyShortExercises = 6
        )

        assertTrue(advice.contains("Great job! You're keeping a consistent Pomodoro routine. Keep it up!"))
        assertTrue(advice.contains("Wow! You've completed over 100 Pomodoros. Your dedication is impressive!"))
        assertTrue(advice.contains("You're doing a good amount of long breathing exercises. Keep maintaining this habit!"))
        assertTrue(advice.contains("You're doing a good amount of short breathing exercises. Keep managing your stress effectively!"))
        assertTrue(advice.contains("Balance your work and relaxation for better long-term productivity. Don't forget to take breaks!"))

        assertTrue(advice.size == 5)
    }


    @Test
    fun `test advice when weekly Pomodoros are more than 20`() {
        val advice = generateAdvice(
            totalPomodoros = 200,
            totalLongExercises = 25,
            totalShortExercises = 15,
            weeklyPomodoros = 30,
            weeklyLongExercises = 5,
            weeklyShortExercises = 10
        )

        assertTrue(advice.contains("You're doing a lot of Pomodoros this week! Remember to take breaks to avoid burnout."))
        assertTrue(advice.contains("Wow! You've completed over 100 Pomodoros. Your dedication is impressive!"))
        assertTrue(advice.contains("Great job on prioritizing relaxation with long breathing exercises. Your mental well-being will thank you!"))
        assertTrue(advice.contains("You're consistently doing short breathing exercises. Great job on managing your daily stress!"))
        assertTrue(advice.contains("Balance your work and relaxation for better long-term productivity. Don't forget to take breaks!"))
        assertTrue(advice.contains("You're doing a lot of work! Make sure to take enough breaks to avoid burnout."))

        assertTrue(advice.size == 6)
    }

    @Test
    fun `test advice when total Pomodoros are less than 50`() {
        val advice = generateAdvice(
            totalPomodoros = 40,
            totalLongExercises = 10,
            totalShortExercises = 5,
            weeklyPomodoros = 12,
            weeklyLongExercises = 3,
            weeklyShortExercises = 4
        )

        assertTrue(advice.contains("Great job! You're keeping a consistent Pomodoro routine. Keep it up!"))
        assertTrue(advice.contains("You're just getting started. Keep building your Pomodoro habit for long-term success."))
        assertTrue(advice.contains("You're doing a good amount of long breathing exercises. Keep maintaining this habit!"))
        assertTrue(advice.contains("You're doing a good amount of short breathing exercises. Keep managing your stress effectively!"))
        assertTrue(advice.contains("You're maintaining a good balance between work and relaxation. Keep it up!"))

        assertTrue(advice.size == 5)
    }

    @Test
    fun `test advice when weekly long exercises are less than 3`() {
        val advice = generateAdvice(
            totalPomodoros = 75,
            totalLongExercises = 12,
            totalShortExercises = 6,
            weeklyPomodoros = 18,
            weeklyLongExercises = 2,
            weeklyShortExercises = 4
        )

        assertTrue(advice.contains("Great job! You're keeping a consistent Pomodoro routine. Keep it up!"))
        assertTrue(advice.contains("You've completed over 50 Pomodoros. Keep pushing toward your goals!"))
        assertTrue(advice.contains("Consider doing more long breathing exercises to help you relax and recharge."))
        assertTrue(advice.contains("You're doing a good amount of short breathing exercises. Keep managing your stress effectively!"))
        assertTrue(advice.contains("Balance your work and relaxation for better long-term productivity. Don't forget to take breaks!"))

        assertTrue(advice.size == 5)
    }


    @Test
    fun `test advice when weekly short exercises are more than 5`() {
        val advice = generateAdvice(
            totalPomodoros = 95,
            totalLongExercises = 18,
            totalShortExercises = 9,
            weeklyPomodoros = 22,
            weeklyLongExercises = 4,
            weeklyShortExercises = 7
        )

        assertTrue(advice.contains("You're doing a lot of Pomodoros this week! Remember to take breaks to avoid burnout."))
        assertTrue(advice.contains("Wow! You've completed over 100 Pomodoros. Your dedication is impressive!"))
        assertTrue(advice.contains("You're doing a good amount of long breathing exercises. Keep maintaining this habit!"))
        assertTrue(advice.contains("You're on your way to mastering long breathing exercises. Keep going!"))
        assertTrue(advice.contains("You're doing a good amount of short breathing exercises. Keep managing your stress effectively!"))
        assertTrue(advice.contains("Balance your work and relaxation for better long-term productivity. Don't forget to take breaks!"))
        assertTrue(advice.contains("You're doing a lot of work! Make sure to take enough breaks to avoid burnout."))

        assertTrue(advice.size == 7)
    }

}
