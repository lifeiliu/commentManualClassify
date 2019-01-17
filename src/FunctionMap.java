import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FunctionMap {

    public String functionName;
    public String fileName;
    public String className;
    public int startLine;
    public int endLine;
    public List<Comment> comments;
    public List<CommentForCat> commentForCats;
    public String fullName;


    private FunctionMap(String functionName, int startLine, int endLine, List<Comment> comments,String fullName) {
        this.functionName = functionName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.comments = comments;
        this.commentForCats = new ArrayList<>();
        this.fileName = fullName;
    }

    public static FunctionMap createFunctionMap(MethodDeclaration md) {
        String functionName = md.getNameAsString();
        int startLine = md.getRange().get().begin.line;
        int endLine = md.getRange().get().end.line;
        String fullName = md.getDeclarationAsString();

        List<Comment> comments = md.getAllContainedComments();

        return new FunctionMap(functionName, startLine, endLine, comments,fullName);
    }

    public void mapCommentForCatsToFunction(CommentForCat[] commentForCats){
        for(CommentForCat commentForCat :commentForCats){
           if(commentForCat.lineStartNumber > this.startLine && commentForCat.lineEndNumber < this.endLine){
               this.commentForCats.add(commentForCat);

           }
        }
    }

}
