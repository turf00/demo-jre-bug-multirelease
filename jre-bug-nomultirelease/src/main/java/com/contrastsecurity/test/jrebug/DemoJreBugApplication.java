package com.contrastsecurity.test.jrebug;

import java.net.URL;

public class DemoJreBugApplication {

  public static void main(final String[] args) {
    reproduce();
  }

  static void reproduce() {
    // log out running Java version based on multi release jar
    JavaVersionSpecific.logVersion();

    final String resourceToGet = "";
    // Fetch an empty resource string from the Classloader
    // When working as expected this will return null
    // Tested with Java 11
    final ClassLoader classLoader = DemoJreBugApplication.class.getClassLoader();
    final URL resource = classLoader.getResource(resourceToGet);
    if (resource == null) {
      System.out.println("Null resource found, working as expected");
    } else {
      System.out.println("Non-null resource found, assumed bug. Resource: " + resource);
    }
  }
}
