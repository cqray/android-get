package cn.cqray.android.`object`

open class VarReference<V : Any> : java.io.Serializable {

    constructor()

    constructor(value: V)

    private lateinit var value: V

    fun set(value: V) = value.also { this.value = it }

    fun get() = value
}