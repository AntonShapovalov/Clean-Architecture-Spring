package clean.architecture.omdb.coroutines

import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DispatcherProviderTest {

    @Test
    fun `when getting IO dispatcher, given provider, then return IO coroutines dispatcher`() {
        // Given
        val provider = DispatcherProvider()

        // When
        val dispatcher = provider.io

        // Then
        assertEquals(dispatcher, Dispatchers.IO)
    }
}
