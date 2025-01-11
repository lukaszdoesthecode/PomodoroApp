import com.example.pomodojo.functionality.dashboard.Config
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfigTest {

    @Test
    fun `test default values`() {
        val config = Config()

        assertEquals(5, config.shortBreak)
        assertEquals(25, config.focusTime)
        assertEquals(20, config.longBreak)
        assertEquals(2, config.iterations)
    }

    @Test
    fun `test updating values`() {
        var config = Config()

        config = config.copy(shortBreak = 10, focusTime = 30, longBreak = 15, iterations = 5)

        assertEquals(10, config.shortBreak)
        assertEquals(30, config.focusTime)
        assertEquals(15, config.longBreak)
        assertEquals(5, config.iterations)
    }
}
