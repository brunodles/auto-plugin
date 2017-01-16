[![Build Status](https://travis-ci.org/brunodles/auto-plugin.svg?branch=release)](https://travis-ci.org/brunodles/auto-plugin)
# AutoPlugin
A configuration/metadata generator for gradle plugin.

## What is it?

This is a library to help you to create your gradle plugins.
It will help you generating the plugin configuration, the `META-INF` files.

## How to use

Just put the annotation on your plugin.

```java
package this.is.mypackage;
@AutoPlugin
public class MyPlugin {

}
```

When you compile the code it will generate a file in this path `resources/META-INF/services/this.is.mypackage.MyPlugin`.
The content of this file will be
```properties
implementation-class=this.is.mypackage.MyPlugin
```

You can also create aliases for you plugins, just add it on the annotation.
```java
package this.is.mypackage;
@AutoPlugin("alias1")
public class MyPlugin {

}
```
```java
package this.is.mypackage;
@AutoPlugin({"alias1", "alias2", "alias3"...})
public class MyPlugin {

}
```

## Dependency

```gradle
provided 'com.brunodles:autoplugin-annotation:1.0.0'
apt 'com.brunodles:autoplugin-processor:1.0.0'
```

## Sources

This lib is inspired by Google Auto libraries.

* [Gradle Plugins](https://docs.gradle.org/current/userguide/plugins.html)
* [Writing Custom Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)
* [Google Auto](https://github.com/google/auto)
* [Google Auto Service](https://github.com/google/auto/tree/master/service)