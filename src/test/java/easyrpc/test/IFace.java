package easyrpc.test;/*
 * ----------------------------------------------------------------------------
 * This code is distributed under a Beer-Ware license
 * ----------------------------------------------------------------------------
 * Mario Macias wrote this file. Considering this, you can do what the fuck you
 * want: modify it, distribute it, sell it, etc. But you MUST always credit me
 * as the original author of this code. In addition, if we met some day and you
 * think this code was useful to you, you MUST pay me a beer (a good one, if
 * possible) as reward for my contribution.
 *
 * Mario Macias Lloret, 2014
 * ----------------------------------------------------------------------------
 */

import java.util.List;
import java.util.Map;

/**
 * Created by mmacias on 08/02/14.
 */
public interface IFace {
    int add(int a, int b);
    String concat(String s1, String s2);
    void doSomeStupidStuff(String str);
    void doSomething();

    FakeClass getFake(long l, String s, char c, OtherFake o);

    int[] doubleArray(int[] arr);

    List<String> asString(int[] arr);

    Map<String,Integer> wordHistogram(String text);

}
