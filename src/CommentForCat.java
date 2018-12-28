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

    private CommentForCat(String text, String commentedCode,CommentLocation location,
                          int lineStartNumber, int lineEndNumber,CommentType type) {
        this.text = text;
        this.commentedCode = commentedCode;
        this.lineStartNumber = lineStartNumber;
        this.lineEndNumber = lineEndNumber;
        this.commentLocation = location;
        this.commentType = type;
    }

    public static CommentForCat convertFromComment(Comment comment){
        String text = comment.getContent();
        CommentType commentType = getCommentType(comment);
        CommentLocation commentLocation = getCommentLocation(comment);
        int lineStartNumber = getLineStartNumber(comment);
        int lineEndNumber = getLineEndNumber(comment);
        String commentedCode = getCommentedCode(comment);
        return new CommentForCat(text,commentedCode,commentLocation,lineStartNumber,lineEndNumber,commentType);

    }

    private static CommentType getCommentType(Comment comment){
        if (comment.isJavadocComment()) return CommentType.DocumentComment;
        else return CommentType.RegularComment;
    }

    private static CommentLocation getCommentLocation(Comment comment){
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

    private static int getLineStartNumber(Comment comment){
        return comment.getRange().get().begin.line;
    }
    private static int getLineEndNumber(Comment comment){
        return comment.getRange().get().end.line;
    }

    private static String getCommentedCode(Comment comment){
        if (! comment.getCommentedNode().isPresent())
            return null;
        return comment.getCommentedNode().get().removeComment().toString();
    }


}
