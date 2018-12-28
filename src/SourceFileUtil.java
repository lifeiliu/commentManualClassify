import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class SourceFileUtil {


    public static List<CommentForCat> getCommentsFromFile(File sourceCodeFile) throws Exception {
        List<CommentForCat> result = new LinkedList<>();
        if (!verifySourceCode(sourceCodeFile)) throw new Exception("not java file");
        try {
            CompilationUnit cu = JavaParser.parse(sourceCodeFile);
            List<Comment> comments = cu.getComments();
            List<Comment> combinedComments = combineMultiLineComment(comments);
            for (Comment comment : combinedComments){
                result.add(CommentForCat.convertFromComment(comment));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;

    }

    private static boolean verifySourceCode(File sourceCodeFile){
        String fileName = sourceCodeFile.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            return "java".equals(fileName.substring(fileName.lastIndexOf("." + 1)));
        }
        return false;
    }

    private static List combineMultiLineComment (List<Comment> comments){
        for (int i = 0; i < comments.size() - 1; i++){

            if (comments.get(i).isOrphan() && comments.get(i).getEnd().get().line == comments.get(i+1).getBegin().get().line - 1 ) {
                comments.get(i + 1).setContent(comments.get(i).getContent() + comments.get(i+1).getContent());
                comments.remove(i);
                i--;
            }
        }
        return comments;
    }
}
