package me.flaming.h0rsescript.error

class IndexOutOfBoundsError(index: Int, size: Int): HSError() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "IndexOutOfBoundsException"
        super.message = "Index $index out of bounds for array of size $size"
    }
}