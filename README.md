**_This is an obsolete project_**

This project adds preprocessing capability to java programs. Antpp does not implement a preprocessor, it calls the well-known cpp (C PreProcessor). Antpp is a collection of ant tasks.

### Usage

Copy antpp-bin.jar into the lib directory of Ant.

It is necessary to define the tasks at the beginning of the build.xml
file:

```
<taskdef name="cpp"          classname="hu.jataka.antpp.Cpp"/>
<taskdef name="cppdel"       classname="hu.jataka.antpp.CppDel"/>
```
