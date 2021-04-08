package cryptTool;

import enums.Action;
import enums.Algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class CryptTool {
    private String output;

    public CryptTool(Algorithm algorithm, Action action, String message, String key) {
        final String fileString;
        final String algoName;
        final Object[] constructorArgs;
        final String methodName;
        final String cryptMessage;

        switch (algorithm) {
            case Shift -> {
                switch (action) {
                    case encrypt, decrypt -> {
                        fileString = "shift";
                        algoName = "CaesarCipher";
                        constructorArgs = new Object[]{Integer.parseInt(key)};
                        methodName = action == Action.encrypt ? "encrypt" : "decrypt";
                        cryptMessage = message;
                    }
                    case crack -> {
                        fileString = "shift";
                        algoName = "CaesarCracker";
                        constructorArgs = new Object[0];
                        methodName = "crack";
                        cryptMessage = message;
                    }
                    default -> {
                        fileString = null;
                        algoName = null;
                        constructorArgs = new Object[0];
                        methodName = null;
                        cryptMessage = null;
                    }
                }
                output = crypt(fileString, algoName, constructorArgs, methodName, cryptMessage);
            }
            case RSA -> {
                BigInteger e = BigInteger.ONE;
                BigInteger n = BigInteger.ONE;
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("keys/" + key + ".txt"));
                    String keyString = reader.readLine();
                    reader.close();
                    String[] keyStrings = keyString.split(";");
                    e = new BigInteger(keyStrings[0]);
                    n = new BigInteger(keyStrings[1]);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                switch (action) {
                    case encrypt, decrypt -> {

                        fileString = "rsa";
                        algoName = "Cipher";
                        constructorArgs = new Object[]{e, n};
                        methodName = action == Action.encrypt ? "encrypt" : "decrypt";
                        cryptMessage = message;
                        output = crypt(fileString, algoName, constructorArgs, methodName, cryptMessage);
                    }
                    case crack -> {
                        fileString = "rsaCracker";
                        algoName = "RSACracker";
                        constructorArgs = new Object[]{e, n};
                        methodName = "execute";
                        cryptMessage = message;
                        Thread thread = new Thread(() -> output = crypt(fileString, algoName, constructorArgs, methodName, cryptMessage));
                        try {
                            thread.start();
                            int counter = 0;
                            while (thread.isAlive() && counter <= 300) {
                                Thread.sleep(100);
                                counter++;
                            }
                            if (thread.isAlive()) {
                                thread.stop();
                                output = "cracking encrypted message \"" + cryptMessage + "\" failed;";
                            }
                        } catch (InterruptedException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            }
        }
    }

    private String crypt(String fileString, String algoName, Object[] constructorArgs, String methodName, String cryptMessage) {
        try {
            URL[] urls = {new URI("jar:file:jars/" + fileString + ".jar!/").toURL()};
            Class aClass = URLClassLoader.newInstance(urls).loadClass(algoName);
            Object instance = aClass.getDeclaredConstructors()[0].newInstance(constructorArgs);
            Method method = aClass.getDeclaredMethod(methodName, String.class);
            return (String) method.invoke(instance, cryptMessage);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public String getOutput() {
        return output;
    }
}
