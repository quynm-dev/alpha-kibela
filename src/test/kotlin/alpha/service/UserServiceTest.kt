@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package alpha.service

import alpha.common.Role
import alpha.common.ServiceType
import alpha.common.Status
import alpha.data.`object`.UserObject
import alpha.error.CodeFactory
import alpha.helper.mock
import alpha.repository.UserRepository
import com.github.michaelbull.result.unwrap
import com.github.michaelbull.result.unwrapError
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class UserServiceTest {
    companion object {
        private val userRepo = mockk<UserRepository>()
        private lateinit var userService: UserService

        @JvmStatic
        @BeforeAll
        fun setUp() {
            userService = UserService(userRepo)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            clearAllMocks()
        }
    }

    @Test
    fun findAll_ReturnAllUsers() = runBlocking {
        val id = 1
        val username = "starkusername"
        val password = "password"
        val name = "Tony Stark"
        val now = LocalDateTime.now()
        val userObjects = listOf(
            UserObject(
                id = 1,
                username = username,
                password = password,
                name = name,
                serviceType = ServiceType.STANDARD.number,
                role = Role.USER.number,
                status = Status.ACTIVE.number,
                createdAt = now,
                updatedAt = now
            )
        )

        coEvery { userRepo.findAll() } returns userObjects

        val result = userService.findAll().unwrap()
        assertEquals(result.size, 1)
        assertEquals(result.first().id, id)
        assertEquals(result.first().username, username)
        assertEquals(result.first().createdAt, now)
        assertEquals(result.first().updatedAt, now)
    }

    @Test
    fun findAll_ReturnExposedSQLException() = runBlocking {
        val e = ExposedSQLException::class.mock()

        coEvery { userRepo.findAll() } throws e

        val error = userService.findAll().unwrapError()
        assertEquals(error.code, CodeFactory.USER.DB_ERROR)
        assertEquals(error.message, "Failed to get all users")
    }

    @Test
    fun findAll_ReturnException() = runBlocking {
        val e = Exception::class.mock()

        coEvery { userRepo.findAll() } throws e

        val error = userService.findAll().unwrapError()
        assertEquals(error.code, CodeFactory.USER.INTERNAL_SERVER_ERROR)
        assertEquals(error.message, "Unexpected error occurred")
    }
}