import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public String methodSignature;
    public String filteredMethodSummary;

    public CommentForCat(String text, String fileName) {
        this.text = text;
        this.fileName = fileName;
    }

    private CommentForCat(String text, String commentedCode,CommentLocation location,
                          int lineStartNumber, int lineEndNumber,CommentType type
                          ) {
        this.text = text;
        this.commentedCode = commentedCode;
        this.lineStartNumber = lineStartNumber;
        this.lineEndNumber = lineEndNumber;
        this.commentLocation = location;
        this.commentType = type;

    }

    public CommentForCat(String text, String commentedCode,CommentLocation location,
                         int lineStartNumber, int lineEndNumber,CommentType type,
                         String methodSignature, String filteredMethodSummary) {
        this.text = text;
        this.commentedCode = commentedCode;
        this.lineStartNumber = lineStartNumber;
        this.lineEndNumber = lineEndNumber;
        this.commentLocation = location;
        this.commentType = type;
        this.methodSignature = methodSignature;
        this.filteredMethodSummary = filteredMethodSummary;
    }

    public void setCommentCategory (CommentCategory category){
        this.commentCategory = category;
    }

    public static CommentForCat convertFromComment(final Comment comment){
        String methodSignature = getMethodSignature(comment);
        String filteredSummary = getFilteredmethodSummary(comment);
        String text = comment.getContent();
        CommentType commentType = getCommentType(comment);
        CommentLocation commentLocation = getCommentLocation(comment);
        int lineStartNumber = getLineStartNumber(comment);
        int lineEndNumber = getLineEndNumber(comment);
        String commentedCode = getCommentedCode(comment);


        return new CommentForCat(text,commentedCode,commentLocation,lineStartNumber,lineEndNumber,commentType,
                methodSignature,filteredSummary);

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

    public static String getMethodSignature(Comment comment){

        String signature = " ";
        if(comment.getCommentedNode().isPresent()) {

            Node commentedCode = comment.getCommentedNode().get();
            if (commentedCode instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) commentedCode;
                Set<String> signatureWords = MethodUtil.getSignatureWords(md);

                for (String word : signatureWords) {
                    signature += word;
                }
            }
        }

        return signature;
    }


    public static String getFilteredmethodSummary(Comment comment){
        String result = " ";
        List<String> methodwords = new ArrayList<>();

        if(comment.getCommentedNode().isPresent()) {
            Node commentedCode = comment.getCommentedNode().get();
            if (commentedCode instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) commentedCode;
                Set<String> methodSummary = SWUM.generateMethodSummary(md);
                for(String statement : methodSummary){
                    methodwords.addAll(WordsUtil.splitSentenceAndCamelWord(statement));
                }
                List <String> filteredWords = WordsUtil.filterStopWords(methodwords,WordsUtil.generateStopWords());
                for(String word : filteredWords){
                    result += word;
                }

                result += getMethodSignature(comment);
            }

        }
        return result;
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

    public static void main(String[] args) throws FileNotFoundException {
        String path ="/home/ggff/Desktop/sourceCode/android/ActivityThread.java";

        File sourceFile = new File(path);
        CompilationUnit cu = JavaParser.parse(sourceFile);
        List<Comment> comments = cu.getComments();

        for (Comment comment: comments){

            CommentForCat commentForCat = convertFromComment(comment);

            //String summary = getMethodSignature(comment);
            //System.out.println(summary);
            /*if(summary != " "){
                CommentForCat commentForCat = convertFromComment(comment);
               // commentForCat.methodSignature = summary;
                System.out.println(commentForCat.methodSignature);
                System.out.println(summary);
            }*/

        }
    }

}
