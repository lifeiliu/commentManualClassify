import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

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
                          int lineStartNumber, int lineEndNumber,CommentType type,
                          String className,String methodName) {
        this.text = text;
        this.commentedCode = commentedCode;
        this.lineStartNumber = lineStartNumber;
        this.lineEndNumber = lineEndNumber;
        this.commentLocation = location;
        this.commentType = type;
        this.className = className;
        this.methodName = methodName;
    }

    public void setCommentCategory (CommentCategory category){
        this.commentCategory = category;
    }

    public static CommentForCat convertFromComment(Comment comment){
        String text = comment.getContent();
        CommentType commentType = getCommentType(comment);
        CommentLocation commentLocation = getCommentLocation(comment);
        int lineStartNumber = getLineStartNumber(comment);
        int lineEndNumber = getLineEndNumber(comment);
        String commentedCode = getCommentedCode(comment);
        String className = getClassName(comment);
        String methodName = getMethodName(comment);

        return new CommentForCat(text,commentedCode,commentLocation,lineStartNumber,lineEndNumber,commentType,className,methodName);

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

    private static String getClassName(Comment comment){
        if(! comment.getCommentedNode().isPresent()){
            return "UNKNOWN_CLASS_NAME";
        }
        Node commentedNode = comment.getCommentedNode().get();
        if (commentedNode instanceof  ClassOrInterfaceDeclaration){
            return ((ClassOrInterfaceDeclaration) commentedNode).getNameAsString();
        }
        while (commentedNode.getParentNode().isPresent()){
            Node parentNode = commentedNode.getParentNode().get();
            if (parentNode instanceof ClassOrInterfaceDeclaration){
                return ((ClassOrInterfaceDeclaration) parentNode).getNameAsString();
            }else
                commentedNode = parentNode;
        }
        return "UNKNOWN_CLASS_NAME";

    }

    private static String getMethodName(Comment comment){
        CommentLocation location = getCommentLocation(comment);
        if (location == CommentLocation.MethodTopComment){
            MethodDeclaration node = (MethodDeclaration) comment.getCommentedNode().get();
            return node.getNameAsString();

        }
        if (location == CommentLocation.MethodFieldComment || location == CommentLocation.MethodInnerComment){
            Node node = comment.getCommentedNode().get();
            while (node.getParentNode().isPresent()){
                Node parent = node.getParentNode().get();
                if (parent instanceof MethodDeclaration){
                    return ((MethodDeclaration) parent).getNameAsString();
                }else
                    node = parent;
            }
        }
        return "UNDEFINED_METHOD_NAME";

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

    public String toString(){
        if (text.length() >= 50){
            return lineStartNumber + text.substring(0,50);
        }
        return lineStartNumber + text;
    }

    public String saveToJson (){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        return json;
    }


}
