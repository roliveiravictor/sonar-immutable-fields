package utils;

import enumerator.AccessModifiers;
import enumerator.Conditionals;
import enumerator.Loops;
import enumerator.Patterns;
import enumerator.ReservedWords;
import immutable.Main;
import java.nio.file.AccessMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author victor.rocha
 * 
 */
public class LineUtils {
    
    public static final boolean LOCK_IMMUTABLE_FIELD = true;
    public static final boolean OPEN_TO_MODIFY = false;

    public static boolean isNotLineToModify(String line) {
        return isAssertEqualsStatement(line) 
                || isConditionalStatement(line) 
                || isIncrementStatement(line) 
                || isLoopStatement(line) 
                || isCommentStatement(line) 
                || hasFinalStatement(line) 
                || hasThisStatement(line) 
                || hasInjectionStatement(line)
                || hasCommaStatement(line)
                || hasImportStatement(line);
    }

    public static boolean isIncrementStatement(String line) {
        return isIncrementPP(line) || isIncrementPE(line);
    }
    
    private static boolean isIncrementPP(String line) {
        return line.contains(Patterns.INCREMENT_PP_WITHOUT_SPACES.getName()) || line.contains(Patterns.INCREMENT_PP_WITH_SPACES.getName());
    }

    private static boolean isIncrementPE(String line) {
        return line.contains(Patterns.INCREMENT_PE_WITHOUT_SPACES.getName()) || line.contains(Patterns.INCREMENT_PE_WITH_SPACES.getName());
    }
    
    public static boolean isConditionalFor(String line){
         return line.contains(Loops.FOR.getName());
    }
    
    public static boolean isConditionalIf(String line){
         return line.contains(Conditionals.IF.getName());
    }
    
    private static boolean isConditionalSwitch(String line) {
        return line.contains(Conditionals.SWITCH.getName());
    }
    
    public static boolean isAttributionLineWithSpaces(String line){
         return line.contains(Patterns.FIELD_ATTRIBUTION_WITH_SPACES.getName());
    }

    private static boolean isAttributionLineWithoutSpaces(String line) {
        return line.contains(Patterns.FIELD_ATTRIBUTION_WITHOUT_SPACES.getName());
    }
    
     public static boolean isEqualsAssertWithSpaces(String line){
         return line.contains(Patterns.EQUALS_ASSERT_WITH_SPACES.getName());
    }

    private static boolean isEqualsAssertWithoutSpaces(String line) {
        return line.contains(Patterns.EQUALS_ASSERT_WITHOUT_SPACES.getName());
    }
    
    private static boolean isCommentStatement(String line) {
        return line.contains(Patterns.COMMENT.getName());
    }

    private static boolean isAssertEqualsStatement(String line) {
        return isEqualsAssertWithSpaces(line) || isEqualsAssertWithoutSpaces(line);
    }

    private static boolean isConditionalStatement(String line) {
        return isConditionalIf(line) || isConditionalSwitch(line);
    }
    
    private static boolean isLoopStatement(String line) {
        return isForStatement(line) || isWhileStatement(line);
    }
    
     private static boolean isForStatement(String line) {
         return line.contains(Loops.FOR.getName());
    }

    private static boolean isWhileStatement(String line) {
        return line.contains(Loops.WHILE.getName());
    }

    private static boolean isAttributionStatement(String line) {
        return isAttributionLineWithSpaces(line) || isAttributionLineWithoutSpaces(line);
    }
    
    private static boolean hasCommaStatement(String line) {
        return line.contains(Patterns.COMMA.getName());
    }
    
    private static boolean isDeclarationStatement(String line) {
        return line.contains(AccessModifiers.FINAL.getName())
               || line.contains(AccessModifiers.PUBLIC.getName())
               || line.contains(AccessModifiers.PRIVATE.getName())
               || line.contains(AccessModifiers.PROTECTED.getName());
    }

    private static boolean hasFinalStatement(String line) {
        return line.contains(AccessModifiers.FINAL.getName());
    }
    
    private static boolean hasThisStatement(String line) {
        return line.contains(Patterns.THIS.getName());
    }
     
    private static boolean hasInjectionStatement(String line) {
        return line.contains(Patterns.INJECTION.getName());
    }
    
    private static boolean hasImportStatement(String line) {
        return line.contains(ReservedWords.IMPORT.getName());
    }
    
    public static String cleanFieldName(String line) {
        String[] separatedLineArray = line.split(Patterns.WHITE_SPACE.getName());
        List<String> wordsFromLine = Arrays.asList(separatedLineArray);
        
        if(isAttributionStatement(line)){
            return getFieldByPattern(wordsFromLine, Patterns.FIELD_ATTRIBUTION_WITHOUT_SPACES.getName());
        }
        
        if(isDeclarationStatement(line)){
            return getFieldByPattern(wordsFromLine, Patterns.BREAKPOINT.getName());
        }
        
        return "";
    }

    private static String getFieldByPattern(List<String> wordsFromLine, String pattern) {
        String lastWord = "";
        
        for(String currentWord : wordsFromLine){
            boolean isCursorAfterField = pattern.equals(currentWord);
            boolean isUselessComponent = currentWord.isEmpty();

            boolean isRegexKey = lastWord.contains(Patterns.BRACKETS_REGEX.getName());
            if(isRegexKey){
                lastWord = lastWord.replace(Patterns.BRACKETS_STRING.getName(), Patterns.BRACKETS_REGEX.getName());
            }
            
            if(isCursorAfterField){
                lastWord = replaceTabsForEmptyString(lastWord);
                return lastWord;
            }else if (!isUselessComponent){ 
                    lastWord = currentWord;
            }
        }
        
        return "";
    }

    public static String insertFinalModifier(Entry<String, Boolean> fieldToModify, String currentLine) {
        String[] separatedLineArray = currentLine.split(Patterns.WHITE_SPACE.getName());
        List<String> wordsFromLine = Arrays.asList(separatedLineArray);
        
        String lastWord = "";
        for(String currentWord : wordsFromLine){
            boolean isUselessComponent = currentWord.isEmpty();
            if(isUselessComponent){
                continue;
            }
            
            boolean isLineWithImmutableModifier = currentWord.equals(fieldToModify.getKey()) && !isNotLineToModify(currentLine);
            if(isLineWithImmutableModifier){
                System.out.println(" ");
                System.out.println("***********************************");
                System.out.println("This is my old line: " + currentLine);

                lastWord = fixRareReplacePatterns(currentWord, lastWord);
                
                String immutableField = AccessModifiers.FINAL.getName();
                boolean isLastWordRightBehindField = !lastWord.isEmpty();
                if(isLastWordRightBehindField){
                    currentLine = currentLine.replaceFirst(lastWord, immutableField + " " + lastWord);
                } else {
                    currentLine = currentLine.replaceFirst(currentWord, immutableField + " " + currentWord);
                }
                
                System.out.println("This is my new line: " + currentLine);
                System.out.println("***********************************");
                System.out.println(" ");
                
                fieldToModify.setValue(LineUtils.LOCK_IMMUTABLE_FIELD);
                
                Main.MODIFIED_LINES_COUNTER++;
                return currentLine;
            }
            
            lastWord = currentWord;
            lastWord = replaceTabsForEmptyString(lastWord);
        }
        
        return currentLine;
    }

    private static String fixRareReplacePatterns(String currentWord, String lastWord) {
        boolean isBracketRegexPattern = lastWord.contains(Patterns.BRACKETS_STRING.getName());
                if(isBracketRegexPattern)
                    lastWord = lastWord.replace(Patterns.BRACKETS_STRING.getName(), Patterns.BRACKETS_REGEX.getName());
                
                boolean isCastVariableRegexPattern = lastWord.contains(Patterns.DOUBLE_LEFT_PARENTHESES_REGEX.getName()) 
                        && currentWord.contains(Patterns.DOUBLE_RIGHT_PARENTHESES_STRING.getName());
                if(isCastVariableRegexPattern){
                    lastWord = lastWord + " " + currentWord;
                }
                
        return lastWord;
    }

    private static void reportFieldInAnalyse(String lastWord) {
        System.out.println(" ");
        System.out.println("###################################");
        System.out.println("This is my field in analysis: " + lastWord);
        System.out.println("###################################");
        System.out.println(" ");
    }

    public static boolean isJSONFile(StringBuilder fileBuilder) {
        return fileBuilder.toString().contains(ReservedWords.ELEMENT.getName())
               || fileBuilder.toString().contains(ReservedWords.COLOR_INT.getName())
               || fileBuilder.toString().contains(ReservedWords.SERIALIZED_NAME.getName());
    }
    
    public static boolean hasIncrementInFile(String fieldName, String file) {
        return file.contains(fieldName + Patterns.INCREMENT_PE_WITHOUT_SPACES.getName())
               || file.contains(fieldName + Patterns.INCREMENT_PE_WITH_SPACES.getName())
               || file.contains(fieldName + Patterns.INCREMENT_PP_WITHOUT_SPACES.getName())
               || file.contains(fieldName + Patterns.INCREMENT_PP_WITH_SPACES.getName());
    }
    
    public static boolean hasDecrementInFile(String fieldName, String file) {
        return file.contains(fieldName + Patterns.DECREMENT_PE_WITHOUT_SPACES.getName())
               || file.contains(fieldName + Patterns.DECREMENT_PE_WITH_SPACES.getName())
               || file.contains(fieldName + Patterns.DECREMENT_PP_WITHOUT_SPACES.getName())
               || file.contains(fieldName + Patterns.DECREMENT_PP_WITH_SPACES.getName());
    }
    
    public static boolean isParameter(String fieldName, String file) {
        return file.contains(fieldName + Patterns.PARAMETER_WITHOUT_SPACES.getName())
               || file.contains(fieldName + Patterns.PARAMETER_WITH_SPACES.getName())
               || file.contains(fieldName + Patterns.PARAMETER_WITH_THROWS.getName());
    }
    
    public static boolean isReservedActivity(String fieldName, String file) {
        return fieldName.equals(ReservedWords.SESSION_CONTROLLER_ACTIVITY.getName());
    }
    
    public static boolean hasEmptySet(String fieldName, String file) {
        return file.contains(fieldName + Patterns.NULL_SET.getName())
               || file.contains(fieldName + Patterns.EMPTY_SET.getName());
    }
    
    public static boolean hasMultipleAssignments(String fieldName, String file) {
        int lastIndex = file.lastIndexOf(fieldName + Patterns.FIELD_ATTRIBUTION_WITH_SPACES.getName());
        int firstIndex = file.indexOf(fieldName + Patterns.FIELD_ATTRIBUTION_WITH_SPACES.getName());
        
        return firstIndex != lastIndex;
    }
     
    public static boolean isReservedStore(String fieldName, String file) {
        return fieldName.equals(ReservedWords.STORE_BACKGROUND.getName())
               || fieldName.equals(ReservedWords.STORE_BAR.getName())
               || fieldName.equals(ReservedWords.STORE_CAMPAIGNS.getName())
               || fieldName.equals(ReservedWords.STORE_TITLE_COLOR.getName())
               || fieldName.equals(ReservedWords.STORE_ID.getName())
               || fieldName.equals(ReservedWords.STORE_CRM.getName())
               || fieldName.equals(ReservedWords.STORE_BODY.getName())
               || fieldName.equals(ReservedWords.STORE_TEXT_COLOR.getName())
               || fieldName.equals(ReservedWords.STORE_MENU.getName())
               || fieldName.equals(ReservedWords.STORE_TILE.getName());
    }
    
    public static boolean isImmutableField(String currentLine, Entry<String, Boolean> fieldToModify) {
        boolean isNotStaticAccessed = !fieldToModify.getKey().contains(Patterns.POINT.getName());
        
        return currentLine.contains(fieldToModify.getKey()) 
                && fieldToModify.getValue() == LineUtils.OPEN_TO_MODIFY
                && isNotStaticAccessed;
    }

    public static boolean isAlreadyDeclaredField(String fieldName, String file){

        return file.contains(fieldName + Patterns.BREAKPOINT.getName())
               || file.contains(fieldName + Patterns.COMMA.getName())
               || file.contains(fieldName + Patterns.FIELD_ATTRIBUTION_WITHOUT_SPACES.getName())
               || file.contains(fieldName + Patterns.FIELD_ATTRIBUTION_WITH_SPACES.getName());
    }

    public static String replaceTabsForEmptyString(String s1) {
        return s1.replace(Patterns.TAB.getName(), Patterns.EMPTY_STRING.getName());
    }
}
