package hu.jataka.antpp;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

public final class Cpp extends Task {
    private File mFile;
    private File mDir;
    private String macros;
  
    public Cpp() {}
  
    public void setFile(File file) { 
        mFile = file; 
    }

    public void setDir(File dir) { 
        mDir = dir; 
    }

    public void setMacros(String macros) {
        this.macros = macros;
    }

    public static class XFilesFilter implements FileFilter {
        public boolean accept(File pathname) {
            return (pathname.isDirectory() || pathname.getName().indexOf(".x") != -1)
                && !pathname.getName().endsWith("~");
        }
    }

    public static String convertName(String name) {
        StringBuffer ret = new StringBuffer(name);
        int rindex;
        while( (rindex = ret.toString().indexOf(".x")) != -1 ) {
            ret.replace(rindex,rindex+2,".");
        }
        return new String(ret);
    }

    public void execute() throws BuildException {
        if( (mFile == null && mDir == null) || 
            (mFile != null && mDir != null)) {
            throw new BuildException("Please specify file or dir (only one of them)");
        }
        if( mFile != null ) {
            String mFile2Name = convertName(mFile.getName());
            File mFile2 = new File(mFile.getParent(),mFile2Name);
            if( mFile.lastModified() < mFile2.lastModified() ) {
                return;
            }
            System.out.println("Preprocessing "+mFile.getName());
            try {
                String params = "-C -P ";
                if( macros != null ) {
                    StringTokenizer stoken = new StringTokenizer(macros, ",");
                    while (stoken.hasMoreTokens()) {
                        params += "-D"+stoken.nextToken()+" ";
                    }
 
                }
                Process p = Runtime.getRuntime().exec("cpp "+params+mFile.getAbsolutePath()+" "+mFile2 );
                p.waitFor();
                if( p.exitValue() != 0 ) {
                    System.out.println("xx");
                    throw new BuildException("cpp exitvalue: "+p.exitValue());
                }
                Chmod chmod = new Chmod();
                chmod.setFile(mFile2);
                chmod.setPerm("g-w");
                //                chmod.execute();
            } catch( Exception e ) {
                throw new BuildException(e);
            }
        } else {
            //        log("x"+mFile);
            File files[] = mDir.listFiles(new XFilesFilter());
            for( int i=0; i<files.length; ++i ) {
                //            log("cppdir"+files[i]);
                if( files[i].isDirectory() ) {
                    //                log("dir");
                    Cpp cpp = new Cpp();
                    cpp.setDir(new File(files[i].getAbsolutePath()));
                    cpp.execute();
                } else {
                    //                                log("file");
                    Cpp cpp = new Cpp();
                    cpp.setFile(new File(files[i].getAbsolutePath()));
                    cpp.execute();
                }
            }
        }
    }
}
