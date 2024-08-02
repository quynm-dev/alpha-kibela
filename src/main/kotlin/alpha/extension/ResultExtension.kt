package alpha.extension

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

fun <T : Any> T.wrapResult() = Ok(this)

fun <T : Any> T.wrapError() = Err(this)
