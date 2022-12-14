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

## Reason

The real problem lies in JarFile.getJarEntry(String)

```java
// JarFile
public ZipEntry getEntry(String name) {
    JarFileEntry je = getEntry0(name);
    if (isMultiRelease()) {
        return getVersionedEntry(name, je);
    }
    return je;
}

private JarEntry getVersionedEntry(String name, JarEntry je) {
    if (BASE_VERSION_FEATURE < versionFeature) {
        if (!name.startsWith(META_INF)) {
            // search for versioned entry
            int v = versionFeature;
            while (v > BASE_VERSION_FEATURE) {
                JarFileEntry vje = getEntry0(META_INF_VERSIONS + v + "/" + name);
                if (vje != null) {
                  return vje.withBasename(name);
                }
              v--;
              }
            }
        }
        return je;
    }
```

You can see that `getVersionedEntry` is triggered if we are dealing with a multi-release jar and therefore it will simply append the input name to the path and then check if that exists.
For empty string it will always be non-null, if the running version of Java has any code to override for that version.
