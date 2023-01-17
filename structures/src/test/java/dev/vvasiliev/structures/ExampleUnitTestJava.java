package dev.vvasiliev.structures;

import org.junit.Test;

import java.util.TreeMap;
import java.util.TreeSet;

public class ExampleUnitTestJava {
    @Test
    public void variance() {
        CottonType cotton = new CottonType();
        FabricType fabric = new FabricType();
        cotton.setValue(new Cotton("Cotton"));
        fabric.setValue(new Fabric("Fabric"));

        System.out.println(fabric.value.name);
    }

    @Test
    public void javaStructures() {
        TreeMap<String, Integer> tree = new TreeMap<String, Integer>();
        TreeSet<String> setTree = new TreeSet<String>();
    }

    @Test
    public void threading() throws InterruptedException {

        Object object = new Object();
        Thread executor = new Thread(() -> {
            System.out.println("Current thread executor" + Thread.currentThread().getName());
            synchronized (object) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread executor2 = new Thread(() -> {
            System.out.println("Current thread executor2" + Thread.currentThread().getName());
            synchronized (object) {
                try {
                    object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Notified");
            }
        });

        executor2.start();
        executor.start();
        executor.join();
        executor2.join();
    }

}

class Fabric {
    String name;

    Fabric(String name) {
        this.name = name;
    }
}

class Cotton extends Fabric {
    Cotton(String name) {
        super(name);
    }
}

class FabricType implements MyJavaType<Fabric> {

    Fabric value = new Fabric("Fabric");

    @Override
    public void setValue(Fabric value) {
        this.value.name = value.name;
    }

    @Override
    public Fabric readValue() {
        return value;
    }
}

class CottonType implements MyJavaType<Cotton> {

    Cotton value = new Cotton("Cotton");

    @Override
    public void setValue(Cotton value) {
        this.value.name = value.name;
    }

    @Override
    public Cotton readValue() {
        return value;
    }
}

interface MyJavaType<Type> {

    void setValue(Type value);

    default void mix(MyJavaType<? extends Type> parameter) {
        Type value = (Type) parameter.readValue();
        setValue(value);
    }

    Type readValue();
}