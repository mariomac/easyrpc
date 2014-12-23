/*
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

package easyrpc.test.httpjson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mmacias on 08/02/14.
 */
public class Implementation implements IFace {

    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public String concat(String s1, String s2) {
        return s1+s2;
    }

    @Override
    public void doSomeStupidStuff(String str) {
        System.out.println("doSomeStupidStuff = " + str);
    }

    @Override
    public void doSomething() {
        System.out.println("Doing something...");
    }

    @Override
    public FakeClass getFake(long l, String s, char c, OtherFake o) {
        FakeClass f = new FakeClass();
        f.property1 = l;
        f.stringProperty = s;
        f.charProperty = c;
        f.other = o;
        return f;
    }

    @Override
    public int[] doubleArray(int[] arr) {
        for(int i = 0 ; i < arr.length ; i++) {
            arr[i] = arr[i] * 2;
        }
        return arr;
    }

    @Override
    public List<String> asString(int[] arr) {
        List<String> ret = new ArrayList<>();
        for(int i : arr) {
            ret.add(String.valueOf(i));
        }
        return ret;
    }

    @Override
    public Map<String,Integer> wordHistogram(String text) {
        Map<String,Integer> hist = new HashMap<>();
        for(String w : text.split(" ")) {
            Integer c = hist.get(w);
            if(c == null) {
                c = 0;
            }
            c++;
            hist.put(w,c);
        }
        return hist;
    }
}
