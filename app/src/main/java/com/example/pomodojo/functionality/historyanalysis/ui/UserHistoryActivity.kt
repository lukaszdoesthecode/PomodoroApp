package com.example.pomodojo.functionality.historyanalysis.ui
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodojo.functionality.dashboard.ui.HomeActivity
import com.example.pomodojo.functionality.historyanalysis.screen.UserHistoryScreen
import com.example.pomodojo.functionality.historyanalysis.viewmodel.UserHistoryViewModel

class UserHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MaterialTheme {
            Surface {
                HistoryScreenWithViewModel()
            }
        }
    }
    }

    @Composable
    fun HistoryScreenWithViewModel(viewModel: UserHistoryViewModel = viewModel()){
        val navigateToHome = viewModel.navigateToHome.observeAsState()

        navigateToHome.value?.let {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            viewModel.resetNavigation()
        }
        UserHistoryScreen(viewModel = viewModel)
    }
}
