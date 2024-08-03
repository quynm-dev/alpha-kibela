package alpha.extension

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun <T : Any> T.wrapResult() = Ok(this)

fun <T : Any> T.wrapError() = Err(this)

inline fun <V, E> Result<V, E>.then(block: (Err<E>) -> V): V {
    return when (this) {
        is Ok -> this.value
        is Err -> block(this)
    }
}