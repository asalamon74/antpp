package hu.jataka.antpp;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

public final class CppDel extends Task {
    private File mDir;
  
    public CppDel() {}
  
    public void setDir(File dir) {
        mDir = dir; 
    }

    public void execute() throws BuildException {        
        File files[] = mDir.listFiles(new Cpp.XFilesFilter());
        for( int i=0; i<files.length; ++i ) {
            if( files[i].isDirectory() ) {
                CppDel cd = new CppDel();
                cd.setDir(new File(files[i].getAbsolutePath()));
                cd.execute();
            } else {
                File file = new File(Cpp.convertName(files[i].getAbsolutePath()));
                if( file.exists() ) {
                    log("Deleting: "+file.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }
}
