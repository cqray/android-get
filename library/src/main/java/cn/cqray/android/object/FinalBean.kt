package cn.cqray.android.`object`

open class FinalBean<T : Any> : java.io.Serializable {

    private lateinit var value: T

    fun set(value: T) = value.also { this.value = it }

    fun get() = value
}

class FinalBoolean : FinalBean<Boolean>() {
    init {
        set(false)
    }
}