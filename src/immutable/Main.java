package immutable;

import java.io.File;
import java.util.ArrayList;

import model.FileRunnerModel;
import model.DirectoryModel;
import model.ResourceModel;
import enumerator.Patterns;
import enumerator.ReservedWords;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import utils.FileUtils;
import utils.LineUtils;

public class Main {

    public static Integer MODIFIED_LINES_COUNTER = 0;

    public static void main(String[] args) {
        try {
            ArrayList<File> classes = getClasses();
            setImmutableFields(classes);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ArrayList<File> getClasses(){
        DirectoryModel directoryClass = new DirectoryModel();

        directoryClass.setPath("/Users/victor.rocha/Documents/Santander/Scrip Test");

        FileUtils classes = new FileUtils();

        ArrayList<File> project = new ArrayList<File>();
        project.addAll(classes.walkDirs(directoryClass.getPath()));
        
        System.out.println("Classes and Layouts: " + project.size() + "\n");
        
        return project;
    }

    private static void setImmutableFields(ArrayList<File> classes) throws FileNotFoundException, IOException {
        for(File clazz : classes) {
            if(fileNeedsModification(clazz)){
                StringBuilder fileBuilder = readFileModifications(clazz);
                writeFileModifications(clazz, fileBuilder);
            }
        }
        
        System.out.println("");
        System.out.println("Lines Modified: " + MODIFIED_LINES_COUNTER);
    }

    private static StringBuilder readFileModifications(File clazz) throws FileNotFoundException {
        FileReader oldClassFile = new FileReader(clazz);
        FileRunnerModel fileRunnerModel = new FileRunnerModel();
        fileRunnerModel.setReader(new BufferedReader(oldClassFile));
            
        StringBuilder fileBuilder = new StringBuilder();
        
        searchFieldsToModify(fileBuilder, fileRunnerModel);
               
        FileReader newClassFile = new FileReader(clazz);
        fileRunnerModel.setReader(new BufferedReader(newClassFile));
        
        fileBuilder.setLength(0);
        fileBuilder.append(getModifiedClassFile(fileRunnerModel));
        
        return fileBuilder;
    }
    
    private static void writeFileModifications(File clazz, StringBuilder fileBuilder) throws IOException {
        FileWriter fileWriter = new FileWriter(clazz);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(fileBuilder.toString());
        bufferedWriter.close();
    }
    
    private static void searchFieldsToModify(StringBuilder fileBuilder, FileRunnerModel fileRunnerModel) {
        Map<String, Boolean> fields = new HashMap<String, Boolean>();

        while (hasLinesToReadInFile(fileRunnerModel)) {
            String currentLine = fileRunnerModel.getLine();
            boolean isModifyField = !LineUtils.isNotLineToModify(currentLine);
            if(isModifyField){
                String fieldName = LineUtils.cleanFieldName(currentLine);

                boolean fieldHasPattern = !fieldName.isEmpty();
                if(fieldHasPattern){
                    boolean isFieldToModify = LineUtils.isAlreadyDeclaredField(fieldName, fileBuilder.toString());
                    if(isFieldToModify){
                        fields.remove(fieldName, LineUtils.OPEN_TO_MODIFY);
                    } else {
                        fields.put(fieldName, LineUtils.OPEN_TO_MODIFY);
                    }
                }
            }

            fileBuilder.append(currentLine + "\n");
        }
        
        cleanModifictionsList(fields, fileBuilder);
        
        boolean isNotBlockedFileToModify = !LineUtils.isJSONFile(fileBuilder);
        if(isNotBlockedFileToModify)
            fileRunnerModel.getModificationsBuffer( ).putAll(fields);
        
    }
    
    private static boolean hasLinesToReadInFile(FileRunnerModel fileRunnerModel) {
        try {
            String text = fileRunnerModel.getReader().readLine();
            fileRunnerModel.setLine(text);
            
            return text != null;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    private static boolean fileNeedsModification(File clazz) throws FileNotFoundException {
        FileReader fileReader = new FileReader(clazz);
        FileRunnerModel fileRunnerModel = new FileRunnerModel();

        fileRunnerModel.setReader(new BufferedReader(fileReader));

        while (hasLinesToReadInFile(fileRunnerModel)) {
            boolean isModifyField = !LineUtils.isNotLineToModify(fileRunnerModel.getLine());
            if(isModifyField){    
                return true;
            }
        }

        return false;
    }

    private static String getModifiedClassFile(FileRunnerModel fileRunnerModel) {
        StringBuilder newClassFile = new StringBuilder();
       
        while (hasLinesToReadInFile(fileRunnerModel)) {
            String currentLine = fileRunnerModel.getLine();
            Set<Map.Entry<String, Boolean>> modificationsBuffer = fileRunnerModel.getModificationsBuffer().entrySet();
           
            for(Map.Entry<String, Boolean> fieldToModify : modificationsBuffer){
                if(LineUtils.isImmutableField(currentLine, fieldToModify)){
                    try{
                        currentLine = LineUtils.insertFinalModifier(fieldToModify, currentLine);
                    } catch (Exception e) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, e.getMessage());
                        Logger.getLogger(" Current line failed " + currentLine);
                    }
                }
            }
            
            newClassFile.append(currentLine + "\n");
        }
        
        return newClassFile.toString();
    }
    
    private static void removeLastLineBreak(StringBuilder fileBuilder) {
        fileBuilder.deleteCharAt(fileBuilder.toString().indexOf("\n"));
        fileBuilder.deleteCharAt(fileBuilder.toString().lastIndexOf("\n"));
    }
    
    private static void reportFieldsToModify(Map<String, Boolean> fields) {
        Integer count = 0;
            for(Map.Entry<String, Boolean> field : fields.entrySet()){
                count++;
                
                System.out.println("--------------------------------------");
                System.out.println("This field  is a final field: " + field.getKey());
                System.out.println("--------------------------------------");
            }
            
            System.out.println("");
            System.out.println("Total of " + count + " fields to modify");
            System.out.println("");
    }

    private static void cleanModifictionsList(Map<String, Boolean> fields, StringBuilder fileBuilder) {
        Map<String, Boolean> fieldsCopy = new HashMap<String, Boolean>();
        fieldsCopy.putAll(fields);
        
        for(Map.Entry<String, Boolean> field : fieldsCopy.entrySet()){
            String parsedField = LineUtils.replaceTabsForEmptyString(field.getKey());

            String setterName = ReservedWords.SETTER.getName() + StringUtils.capitalize(parsedField);
            boolean hasSetterInFile = fileBuilder.toString().contains(setterName);
            if(hasSetterInFile){
                fields.remove(field.getKey());
            }
            
            boolean hasIncrementInFile = LineUtils.hasIncrementInFile(parsedField, fileBuilder.toString());
            if(hasIncrementInFile){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
            
            boolean hasDecrementInFile = LineUtils.hasDecrementInFile(parsedField, fileBuilder.toString());
            if(hasDecrementInFile){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
            
            boolean isParameter = LineUtils.isParameter(parsedField, fileBuilder.toString());
            if(isParameter){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
            
            boolean isReservedActivity = LineUtils.isReservedActivity(parsedField, fileBuilder.toString());
            if(isReservedActivity){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
            
            boolean isReservedStore = LineUtils.isReservedStore(parsedField, fileBuilder.toString());
            if(isReservedStore){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
            
            boolean hasEmptySet = LineUtils.hasEmptySet(parsedField, fileBuilder.toString());
            if(hasEmptySet){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
            
            boolean hasMultipleAssignments = LineUtils.hasMultipleAssignments(parsedField, fileBuilder.toString());
            if(hasMultipleAssignments){
                fields.remove(field.getKey(), LineUtils.OPEN_TO_MODIFY);
            }
        }
    }
    
}