package model;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author victor.rocha
 */
public class FileRunnerModel {
    
    private BufferedReader reader;
    private String line;
    private Map<String, Boolean> modificationsBuffer = new HashMap<String, Boolean>();
 
    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
    
    public Map<String, Boolean> getModificationsBuffer(){
        return this.modificationsBuffer;
    }
}
