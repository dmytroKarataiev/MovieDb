/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.adkdevelopment.moviesdb.utils;

import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Created by karataev on 5/6/16.
 */
public class CoreNullnessUtils {

    public static <T> T firstNonNull(T... objs) {
        for (T obj : objs) {
            if (obj != null) {
                return obj;
            }
        }
        throw new NullPointerException();
    }

    @Contract("null -> false")
    public static <T> boolean isNotNull(T obj) {
        return obj != null;
    }

    @Contract("null -> true")
    public static <T> boolean isNull(T obj) {
        return !isNotNull(obj);
    }

    @Contract("null -> false")
    public static boolean isNotNullOrEmpty(List list) {
        return !isNullOrEmpty(list);
    }

    @Contract("null -> true")
    public static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    @Contract("null -> true")
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0; // string.isEmpty() in Java 6
    }

    @Contract("null -> false")
    public static boolean isNotNullOrEmpty(String string) {
        return !isNullOrEmpty(string);
    }

    public static String firstNonNull(String... objs) {
        for (String obj : objs) {
            if (obj != null) {
                return obj;
            }
        }
        throw new NullPointerException();
    }

}