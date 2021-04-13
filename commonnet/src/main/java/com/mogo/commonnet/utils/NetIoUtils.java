package com.mogo.commonnet.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author xuanlonghua
 * @desc
 * @date 2021/2/7
 */

public class NetIoUtils {

    /**
     * 关闭流
     *
     * @param closeable
     * @throws IOException
     */
    public static void closeSilent(Closeable closeable)  {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
