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

    private void real_execute(File dir) {
        File files[] = dir.listFiles(new Cpp.XFilesFilter());
        for( int i=0; i<files.length; ++i ) {
            if( files[i].isDirectory() ) {
                log("Checking "+files[i]+" directory", Project.MSG_VERBOSE);
                real_execute(new File(files[i].getAbsolutePath()));
            } else {
                File file = new File(Cpp.convertName(files[i].getAbsolutePath()));
                if( file.exists() ) {
                    log("Deleting: "+file.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }

    public void execute() throws BuildException {
        if( mDir == null) {
            throw new BuildException("Please specify dir");
        }
        real_execute(mDir);
    }
}
