package alpha.extension

import alpha.error.AppError
import alpha.error.Code

fun AppError.isType(code: Code) = this.code == code