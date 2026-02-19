package clean.architecture.omdb.coroutines

import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DispatchersProviderTest {

    @Test
    fun `when getting IO dispatcher, given provider, then return IO coroutines dispatcher`() {
        // Given
        val provider = DispatchersProvider()

        // When
        val dispatcher = provider.io

        // Then
        assertEquals(dispatcher, Dispatchers.IO)
    }
}
