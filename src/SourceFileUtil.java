import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
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
            return "java".equals(fileName.substring(fileName.lastIndexOf("." )+ 1));
        }
        return false;


    }

    private static List combineMultiLineComment (List<Comment> comments){
        comments.sort(new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getRange().get().begin.line - o2.getRange().get().begin.line;
            }
        });
        if (comments.size() < 1) return comments;
        for (int i = 1; i < comments.size() - 1; i++){

            if ((comments.get(i).isOrphan() || comments.get(i-1).isOrphan()) && comments.get(i-1).getEnd().get().line
                    == comments.get(i).getBegin().get().line - 1 ) {
                comments.get(i).setContent(comments.get(i - 1).getContent() +" "+ comments.get(i).getContent());
                comments.remove(i - 1);
                i--;
            }
        }
        return comments;
    }



    /*public static void main(String[] args){
        System.out.println(verifySourceCode(new File("/home/ggff/Desktop/sourceCode/crawl4j/AuthInfo.java")));
    }*/
}
