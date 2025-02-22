package me.flaming.h0rsescript.errors

class IndexOutOfBoundsError(index: Int, size: Int): H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "IndexOutOfBoundsException"
        super.message = "Index $index out of bounds for array of size $size"
    }
}