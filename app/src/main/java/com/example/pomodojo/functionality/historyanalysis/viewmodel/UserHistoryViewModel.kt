package com.example.pomodojo.functionality.historyanalysis.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class UserHistoryViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    val _totalPomodoros = MutableStateFlow(0)
    val totalPomodoros: StateFlow<Int> = _totalPomodoros

    val _totalLongExercises = MutableStateFlow(0)
    val totalLongExercises: StateFlow<Int> = _totalLongExercises

    val _totalShortExercises = MutableStateFlow(0)
    val totalShortExercises: StateFlow<Int> = _totalShortExercises

    val _weeklyPomodoros = MutableStateFlow(0)
    val weeklyPomodoros: StateFlow<Int> = _weeklyPomodoros

    val _weeklyLongExercises = MutableStateFlow(0)
    val weeklyLongExercises: StateFlow<Int> = _weeklyLongExercises

    val _weeklyShortExercises = MutableStateFlow(0)
    val weeklyShortExercises: StateFlow<Int> = _weeklyShortExercises

    private val _navigateToHome = MutableLiveData<Boolean?>(null)
    val navigateToHome: LiveData<Boolean?> = _navigateToHome

    private val _errorMessage = MutableLiveData<Pair<String, String>?>(null)
    val errorMessage: LiveData<Pair<String, String>?> = _errorMessage

    /**
     * Navigates to the Data Analysis screen.
     */
    fun navigateToHome() {
        viewModelScope.launch {
            try {
                _navigateToHome.value = true
            } catch (e: Exception) {
                _errorMessage.value = Pair("Navigation Error", e.message ?: "Unknown error")
            }
        }
    }

    /**
     *Reset the Navigation.
     */
    fun resetNavigation() {
        viewModelScope.launch {
            try {
                _navigateToHome.value = null
            } catch (e: Exception) {
                _errorMessage.value = Pair("Reset Error", e.message ?: "Unknown error")
            }
        }
    }

    //TODO connect and test
    /**
     * Loads the user's Pomodoro history from the Firestore database.
     *
     * This function retrieves the total and weekly counts of completed Pomodoros, long breathing exercises,
     * and short breathing exercises from the user's Firestore collection. The data is then used to update
     * corresponding state flows to reflect the user's progress and activity.
     *
     * The function performs the following tasks:
     * - Connects to Firestore and accesses the current user's Pomodoro data.
     * - Iterates through the user's records to calculate:
     *   - Total Pomodoros completed.
     *   - Total long breathing exercises completed.
     *   - Total short breathing exercises completed.
     *   - Weekly Pomodoros completed in the last 7 days.
     *   - Weekly long breathing exercises completed in the last 7 days.
     *   - Weekly short breathing exercises completed in the last 7 days.
     * - Updates the ViewModel's state flows with the calculated values.
     *
     * The function handles both success and failure cases when fetching data from Firestore:
     * - On success: Updates the state flows with the aggregated data.
     * - On failure: Handles any errors that occur during data retrieval.
     *
     * Preconditions:
     * - The user must be logged in, and a valid user ID must be available.
     **/
    fun loadUserHistory() {
        viewModelScope.launch {
            if (userId != null) {
                val userDocRef = db.collection("Users").document(userId).collection("Pomodoros")
                userDocRef.get()
                    .addOnSuccessListener { documents ->
                        var pomodorosCount = 0
                        var longExercisesCount = 0
                        var shortExercisesCount = 0

                        var weeklyPomodorosCount = 0
                        var weeklyLongExercisesCount = 0
                        var weeklyShortExercisesCount = 0

                        val oneWeekAgo = Calendar.getInstance().apply {
                            add(Calendar.DATE, -7)
                        }.time

                        for (doc in documents) {
                            val timestamp = doc.getDate("timestamp") ?: continue
                            val pomodoroCount = doc.getLong("pomodoroCount")?.toInt() ?: 0
                            val longExercises = doc.getLong("longExercises")?.toInt() ?: 0
                            val shortExercises = doc.getLong("shortExercises")?.toInt() ?: 0

                            pomodorosCount += pomodoroCount
                            longExercisesCount += longExercises
                            shortExercisesCount += shortExercises

                            if (timestamp.after(oneWeekAgo)) {
                                weeklyPomodorosCount += pomodoroCount
                                weeklyLongExercisesCount += longExercises
                                weeklyShortExercisesCount += shortExercises
                            }
                        }

                        _totalPomodoros.value = pomodorosCount
                        _totalLongExercises.value = longExercisesCount
                        _totalShortExercises.value = shortExercisesCount
                        _weeklyPomodoros.value = weeklyPomodorosCount
                        _weeklyLongExercises.value = weeklyLongExercisesCount
                        _weeklyShortExercises.value = weeklyShortExercisesCount
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }
            }
        }
    }
}


/**
 * Generates personalized advice based on the user's Pomodoro and breathing exercise activity.
 *
 * @param totalPomodoros The total number of Pomodoros completed by the user.
 * @param totalLongExercises The total number of long breathing exercises completed by the user.
 * @param totalShortExercises The total number of short breathing exercises completed by the user.
 * @param weeklyPomodoros The number of Pomodoros completed by the user in the past week.
 * @param weeklyLongExercises The number of long breathing exercises completed by the user in the past week.
 * @param weeklyShortExercises The number of short breathing exercises completed by the user in the past week.
 * @return A list of advice strings tailored to the user's activity levels.
 */
fun generateAdvice(
    totalPomodoros: Int,
    totalLongExercises: Int,
    totalShortExercises: Int,
    weeklyPomodoros: Int,
    weeklyLongExercises: Int,
    weeklyShortExercises: Int
): List<String> {
    val advice = mutableListOf<String>()

    if (totalPomodoros >= 0 && weeklyPomodoros == 0) {
        advice.add("It looks like you haven't done any Pomodoros this week. Try to get back on track!")
    }else{
        if (weeklyPomodoros < 10) {
            advice.add("Try to do more Pomodoros to boost your productivity. Aim for at least 10 sessions per week.")
        } else if (weeklyPomodoros in 10..20) {
            advice.add("Great job! You're keeping a consistent Pomodoro routine. Keep it up!")
        } else {
            advice.add("You're doing a lot of Pomodoros this week! Remember to take breaks to avoid burnout.")
        }

        if (totalPomodoros >= 100) {
            advice.add("Wow! You've completed over 100 Pomodoros. Your dedication is impressive!")
        } else if (totalPomodoros >= 50) {
            advice.add("You've completed over 50 Pomodoros. Keep pushing toward your goals!")
        } else {
            advice.add("You're just getting started. Keep building your Pomodoro habit for long-term success.")
        }

        if (weeklyLongExercises < 3) {
            advice.add("Consider doing more long breathing exercises to help you relax and recharge.")
        } else if (weeklyLongExercises in 3..5) {
            advice.add("You're doing a good amount of long breathing exercises. Keep maintaining this habit!")
        } else {
            advice.add("Great job on prioritizing relaxation with long breathing exercises. Your mental well-being will thank you!")
        }

        if (totalLongExercises >= 50) {
            advice.add("You've completed over 50 long breathing exercises. That's a strong commitment to your well-being!")
        } else if (totalLongExercises >= 20) {
            advice.add("You're on your way to mastering long breathing exercises. Keep going!")
        }

        if (weeklyShortExercises < 5) {
            advice.add("Try to incorporate more short breathing exercises into your routine to reduce daily stress.")
        } else if (weeklyShortExercises in 5..10) {
            advice.add("You're doing a good amount of short breathing exercises. Keep managing your stress effectively!")
        } else {
            advice.add("You're consistently doing short breathing exercises. Great job on managing your daily stress!")
        }

        if (weeklyPomodoros > weeklyLongExercises + weeklyShortExercises) {
            advice.add("Balance your work and relaxation for better long-term productivity. Don't forget to take breaks!")
        } else {
            advice.add("You're maintaining a good balance between work and relaxation. Keep it up!")
        }

        if (weeklyPomodoros > 25) {
            advice.add("You're doing a lot of work! Make sure to take enough breaks to avoid burnout.")
        }
    }

    return advice
}
