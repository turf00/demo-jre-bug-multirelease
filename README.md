# demo-jre-bug-multirelease
Shows a bug with the JRE and getResource with multi release jars when getting a resource that is the empty string.

## Steps to reproduce

There are two modules, one with and one without a mutli-release jar.
You simply build then execute the jar.

The problem is triggered by calling ClassLoader.getResource with the empty string.

E.g.

```java
    final ClassLoader classLoader = DemoJreBugApplication.class.getClassLoader();
    final URL resource = classLoader.getResource("");
    if (resource == null) {
      System.out.println("Null resource found, working as expected");
    } else {
      System.out.println("Non-null resource found, assumed bug. Resource: " + resource);
    }
```

Normally we would expect `getResource("")` to return null but in the case of J11 and above with a multi-release jar, we will get returned the folder in the classpath representing the multi release code for the Java version.

```sh
Java 11 replacement version
Non-null resource found, assumed bug. Resource: jar:file:/Users/..../jre-bug/jre-bug-multirelease/build/libs/jre-bug-multirelease-0.0.1-SNAPSHOT.jar!/META-INF/versions/11/
```
