[![Build Status](https://travis-ci.com/asalamon74/antpp.svg?branch=master)](https://travis-ci.com/asalamon74/antpp)

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

### Cpp

Calls the preprocessor for a specified file, or for all the \*.x\* files
in a directory. It ignores files with the following patterns: \*~, .#\*,
\*.xml

It replaces the '.x' part of the name with '.' 
For example: Main.xjava -> Main.java

It creates read-only files (if the extension is not jad), because
modifying the generated files by hand is not a good idea.

#### Parameters

- file:   Name of the file to be preprocessed.
- dir:    Name of the directory, to search for files to be preprocessed.
- macros: List of macros to be passed to cpp. <macro> tags can also be used.
 
#### Example

```
<target name="preprocess" depends="init">
    <cpp dir="src" macros='DEBUG,VER=100' />
</target>
```

Or:

```
<target name="preprocess" depends="init">
    <cpp dir="src">
        <macro name="DEBUG" />
	<macro name="VER" value="100" />
    </cpp>
</target>
```

### CppDel

Deletes the generated files (useful as a part of the clean process).

#### Parameters

- dir:    Directory to search for generated files.

#### Example

```
<target name="clean">
    <cppdel dir="src" />
</target>
```
