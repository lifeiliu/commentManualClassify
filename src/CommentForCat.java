import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

public class CommentForCat {
    public String text;
    public String commentedCode;
    public String methodName;
    public String fileName;
    public String className;
    public int lineStartNumber;
    public int lineEndNumber;
    public CommentType commentType;
    public CommentLocation commentLocation;
    public CommentCategory commentCategory;

    public CommentForCat(String text, String fileName) {
        this.text = text;
        this.fileName = fileName;
    }

    private CommentForCat(String text, String commentedCode, String methodName,
                          String className, int lineStartNumber, int lineEndNumber) {
        this.text = text;
        this.commentedCode = commentedCode;
        this.methodName = methodName;
        this.className = className;
        this.lineStartNumber = lineStartNumber;
        this.lineEndNumber = lineEndNumber;
    }

    public CommentForCat convertFromComment(Comment comment){
        String text = comment.getContent();
        CommentType commentType = getCommentType(comment);
        CommentLocation commentLocation = getCommentLocation(comment);

    }

    private CommentType getCommentType(Comment comment){
        if (comment.isJavadocComment()) return CommentType.DocumentComment;
        else return CommentType.RegularComment;
    }

    private CommentLocation getCommentLocation(Comment comment){
        if(!comment.getCommentedNode().isPresent()) return  CommentLocation.OrphanComment;
        Node node = comment.getCommentedNode().get();
        if (node instanceof ClassOrInterfaceDeclaration) return CommentLocation.ClassTopComment;
        if (node instanceof MethodDeclaration) return  CommentLocation.MethodTopComment;
        if (node instanceof FieldDeclaration){
            if ( node.getParentNode().get() instanceof ClassOrInterfaceDeclaration)
                return CommentLocation.ClassFieldComment;
            else
                return CommentLocation.MethodFieldComment;
        }
        while (node.getParentNode().isPresent()){
            if (node.getParentNode().get() instanceof MethodDeclaration)
                return CommentLocation.MethodInnerComment;
            node = node.getParentNode().get();
        }
        return CommentLocation.OtherLocation;

    }



}
