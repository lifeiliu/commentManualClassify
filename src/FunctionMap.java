import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.util.List;

public class FunctionMap {

    public String functionName;
    public String fileName;
    public String className;
    public int startLine;
    public int endLine;
    public List<Comment> comments;


    private FunctionMap(String functionName, int startLine, int endLine, List<Comment> comments) {
        this.functionName = functionName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.comments = comments;
    }

    public static FunctionMap createFunctionMap(MethodDeclaration md) {
        String functionName = md.getNameAsString();
        int startLine = md.getRange().get().begin.line;
        int endLine = md.getRange().get().end.line;

        List<Comment> comments = md.getAllContainedComments();

        return new FunctionMap(functionName, startLine, endLine, comments);
    }

}
