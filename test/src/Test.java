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

import easyrpc.client.Instantiator;
import easyrpc.test.IFace;

/**
 * Created by mmacias on 08/02/14.
 */
public class Test {
    public static final void main(String[] args) throws Exception {



        IFace obj = (IFace) new Instantiator().instantiate(IFace.class);

        System.out.println("LLamando a concatena: " + obj.concatena("left","right"));
        System.out.println("Llamando a suma: " + obj.suma(2,3));
        System.out.println("Sacando algo por pantalla: ");
        obj.sacaPorPantalla("Hola Mundo!");

    }
}
