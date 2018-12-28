import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class SourceFileUtil {


    public static List<CommentForCat> getCommentsFromFile(File sourceCodeFile) throws Exception {

        if (!verifySourceCode(sourceCodeFile)) throw new Exception("not java file");
        try {
            CompilationUnit cu = JavaParser.parse(sourceCodeFile);
            List<Comment> comments = cu.getComments();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }

    private static boolean verifySourceCode(File sourceCodeFile){
        String fileName = sourceCodeFile.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            return "java".equals(fileName.substring(fileName.lastIndexOf("." + 1)));
        }
        return false;
    }
}
