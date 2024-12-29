package concept.stc.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.springframework.stereotype.Component

/**
 * Provides coroutines dispatchers.
 * This class allows to replace background dispatchers for unit and integration testing.
 */
@Component
class DispatchersProvider {

    /**
     * Get the IO dispatcher.
     */
    val io: CoroutineDispatcher get() = Dispatchers.IO
}
