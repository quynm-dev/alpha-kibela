package alpha.helper

import io.mockk.InternalPlatformDsl.toStr
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.reflect.KClass

inline fun <reified T : Exception> KClass<T>.mock(
    message: String = this.simpleName.toStr(),
    cause: Exception? = null
): T {
    val exception = mockk<T>()

    coEvery { exception.stackTrace } returns emptyArray()
    coEvery { exception.suppressed } returns emptyArray()
    coEvery { exception.message } returns message
    coEvery { exception.cause } returns cause

    return exception
}