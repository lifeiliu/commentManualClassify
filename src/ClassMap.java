import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.util.List;

public class ClassMap {
    public String className;
    public String fileName;
    public int startLine;
    public int endLine;
    public List<MethodDeclaration> methods;
    public List<Comment> comments;

    private ClassMap(String className, int startLine, int endLine, List<MethodDeclaration> methods,
                    List<Comment> comments) {
        this.className = className;
        this.startLine = startLine;
        this.endLine = endLine;
        this.methods = methods;
        this.comments = comments;
    }

    public static ClassMap getClassMap(ClassOrInterfaceDeclaration clazz){
        String className = clazz.getNameAsString();
        int startLine = clazz.getRange().get().begin.line;
        int endLine = clazz.getRange().get().end.line;
        List<MethodDeclaration> methods = clazz.getMethods();
        List<Comment> comments = clazz.getAllContainedComments();

        return  new ClassMap(className,startLine,endLine,methods,comments);
    }
}
