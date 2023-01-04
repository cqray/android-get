package cn.cqray.android.util;

import butterknife.Unbinder;

/**
 * 第三方框架是否引入检查工具类
 * <p>为什么不使用Kotlin，是因为Kotlin好像能捕获{@link NoClassDefFoundError}异常，
 * 但是却仍然有这个异常抛出，导致程序闪退。</p>
 * @author Cqray
 */
public class ThirdCheckUtils {

    /** 是否支持ButterKnife **/
    private static boolean sButterKnifeSupported = true;
    /** 是否支持Rxjava2 **/
    private static boolean sRxjava2Supported = true;
    /** 是否支持Rxjava3 **/
    private static boolean sRxjava3Supported = true;

    public static void check() {
        checkButterKnife();
        checkRxjava2();
        checkRxjava3();
    }

    /**
     * 检验ButterKnife框架是否引入
     */
    private static void checkButterKnife() {
        try {
            // 检验ButterKnife框架是否引入
            new Unbinder() {
                @Override
                public void unbind() {

                }
            };
            sButterKnifeSupported = true;
        } catch (NoClassDefFoundError e) {
            sButterKnifeSupported = false;
        }
    }

    /**
     * 检验Rxjava2框架是否引入
     */
    private static void checkRxjava2() {
        try {
            // 检验Rxjava2框架是否引入
            new io.reactivex.functions.Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                }
            };
            sRxjava2Supported = true;
        } catch (NoClassDefFoundError e) {
            sRxjava2Supported = false;
        }
    }

    /**
     * 检验Rxjava3框架是否引入
     */
    private static void checkRxjava3() {
        try {
            // 检验Rxjava3框架是否引入
            new io.reactivex.rxjava3.functions.Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                }
            };
            sRxjava3Supported = true;
        } catch (NoClassDefFoundError e) {
            sRxjava3Supported = false;
        }
    }

    public static boolean isButterKnifeSupported() {
        return sButterKnifeSupported;
    }

    public static boolean isRxjava2Supported() {
        return sRxjava2Supported;
    }

    public static boolean isRxjava3Supported() {
        return sRxjava3Supported;
    }
}
