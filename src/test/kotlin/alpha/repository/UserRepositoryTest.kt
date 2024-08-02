@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package alpha.repository

import alpha.config.IRepositoryTest
import io.mockk.clearAllMocks
import kotlin.test.assertEquals
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class UserRepositoryTest {
    companion object : IRepositoryTest() {
        private lateinit var userRepo: UserRepository

        @JvmStatic
        @BeforeAll
        fun setUp() {
            init()
            userRepo = UserRepository()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            clearAllMocks()
            finish()
        }
    }

    @Test
    fun findAll_ReturnAllUsers() = repoRunner {
        val result = userRepo.findAll()
        assertEquals(result.size, 10)
    }
}
