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

    private void real_execute(File file, File dir, String macros) throws BuildException {
        if( file != null ) {
            String mFile2Name = convertName(file.getName());
            File mFile2 = new File(file.getParent(),mFile2Name);
            if( file.lastModified() < mFile2.lastModified() ) {
                return;
            }
            
            if( mFile2.exists() ) {
                // we have to delete because cpp may not be able 
                // to modify it, because of the chmod command.
                mFile2.delete();
            }
                
            log("Preprocessing "+file.getName());
            try {
                String params = "-C -P ";
                if( macros != null ) {
                    StringTokenizer stoken = new StringTokenizer(macros, ",");
                    while (stoken.hasMoreTokens()) {
                        params += "-D"+stoken.nextToken()+" ";
                    }
 
                }
                log("Executing "+"cpp "+params+file.getAbsolutePath()+" "+mFile2, Project.MSG_VERBOSE);
                Process p = Runtime.getRuntime().exec("cpp "+params+file.getAbsolutePath()+" "+mFile2 );
                BufferedReader breader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                p.waitFor();
                if( p.exitValue() != 0 ) {
                    String err="";
                    while( breader.ready() ) {
                        String line = breader.readLine();
                        log(line, Project.MSG_ERR);
                        err += line;                        
                    }
                    throw new BuildException("cpp error "+err);
                }
                // It's not working
//                 Chmod chmod = new Chmod();
//                 chmod.setFile(mFile2);
//                 chmod.setPerm("g-w");
//                 chmod.execute();

                if( !mFile2Name.endsWith(".jad") ) {
                    // ugly hack
                    // will be removed after using antenna
                    String []cmd = new String[3];
                    cmd[0] = "/bin/chmod";
                    cmd[1] = "a-w";
                    cmd[2] = mFile2.getAbsolutePath();
                    try {
                        Process chmodProc = Runtime.getRuntime().exec(cmd);
                        chmodProc.waitFor();
                        if( chmodProc.exitValue() != 0 ) {
                            throw new IOException();
                        }
                    } catch( IOException e ) {
                        // Only Warning
                        log("Chmod unsuccessfull", Project.MSG_WARN);
                    }
                }
            } catch( Exception e ) {
                throw new BuildException(e);
            }
        } else {
            File files[] = dir.listFiles(new XFilesFilter());
            for( int i=0; i<files.length; ++i ) {
                if( files[i].isDirectory() ) {
                    real_execute(null, new File(files[i].getAbsolutePath()), macros);
                } else {
                    real_execute(new File(files[i].getAbsolutePath()), null, macros);
                }
            }
        }        
    }

    public void execute() throws BuildException {
        if( (mFile == null && mDir == null) || 
            (mFile != null && mDir != null)) {
            throw new BuildException("Please specify file or dir (only one of them)");
        }
        real_execute(mFile, mDir, macros);
    }
}