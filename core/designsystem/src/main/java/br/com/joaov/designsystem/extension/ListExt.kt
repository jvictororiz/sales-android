package br.com.joaov.designsystem.extension


fun <T> MutableList<T>.addLast(item: T) {
    if (size == 0) {
        add(item)
    } else {
        add(size - 1, item)
    }
}