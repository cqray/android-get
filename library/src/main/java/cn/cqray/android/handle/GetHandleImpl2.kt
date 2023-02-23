//package cn.cqray.android.handle
//
//import androidx.lifecycle.LifecycleCoroutineScope
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.delay
//import kotlin.coroutines.CoroutineContext
//
//class GetHandleImpl2(private val lifecycleOwner: LifecycleOwner, override val coroutineContext: CoroutineContext) :
//    GetHandle, CoroutineScope {
//
//    override fun timer(tag: Any?, delayed: Int?, func: (() -> Unit)?) {
////        TODO("Not yet implemented")
////        var v = Vertx()
//
//        val lifecycleScope = lifecycleOwner.lifecycleScope
//
////        LifecycleCoroutineScope.launch {
////
////            delay(10)
////        }
//    }
//
//    override fun periodic(
//        tag: Any?,
//        delayed: Int?,
//        duration: Int?,
//        condition: ((Int) -> Boolean)?,
//        func: (() -> Unit)?
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun cancel(tag: Any?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun cancelAll() {
//        TODO("Not yet implemented")
//    }
//}