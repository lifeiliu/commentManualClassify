import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Set;

public class RedundantPostprocessing {
    Comment comment;

    String commentId;
    CommentLocation location;
    double jaccardSimilarity;
    double cosineSimilarity;
    Boolean isRedudant;
    Boolean isJavadoc;

    public RedundantPostprocessing(Comment comment){

        this.commentId = getCommentId(comment);
        this.location = getLocation(comment);
        this.jaccardSimilarity = getJaccardSimilarity(comment);
        this.cosineSimilarity = getCosineSimilarity(comment);
        this.isJavadoc = comment.isJavadocComment();
    }

    public RedundantPostprocessing preprocessing(Comment comment){
        return null;
    }

    private String getCommentId(Comment comment){
        if (comment.findCompilationUnit().isPresent()){
            CompilationUnit cu = comment.findCompilationUnit().get();
            String fileName = cu.getStorage().get().getFileName();
            int lineStart = comment.getBegin().get().line;
            return fileName+lineStart;
        }else{
            return null;
        }

    }

    private CommentLocation getLocation(Comment comment){
        return CommentForCat.getCommentLocation(comment);
    }

    private double getJaccardSimilarity(Comment comment){
        Similarity similarity = new Similarity(3);
        return similarity.jaccardSimilarity(commentFilter(comment),codeSummary(comment));

    }

    private double getCosineSimilarity(Comment comment){
        Similarity similarity = new Similarity(3);
        return  similarity.cosinSimilarity(commentFilter(comment),codeSummary(comment));
    }

    //filter out non-alphanumeric chars
    public static String commentFilter(Comment comment){
        String commentContent = comment.getContent();
        return WordsUtil.filterOutNonAlphaNumeric(commentContent);
    }

    public static String codeSummary(Comment comment){
        String summary = "";
        if (comment.getCommentedNode().isPresent()){
            Node commentedCode = comment.getCommentedNode().get();
            if(commentedCode instanceof MethodDeclaration){
                Set<String> codeSummary = MethodUtil.methodCodeSummary((MethodDeclaration) commentedCode);
                for(String word: codeSummary){
                    summary += word;
                }

            } else
            if(commentedCode instanceof ClassOrInterfaceDeclaration){
                summary = ((ClassOrInterfaceDeclaration) commentedCode).getNameAsString();

            }else
            if(commentedCode instanceof Statement){
                for(Node childNode : commentedCode.getChildNodes()){
                    if (childNode instanceof Comment == false){
                        summary += childNode.toString();
                    }
                }
            }else {
                summary = commentedCode.removeComment().toString();
            }

        }
        return WordsUtil.filterOutNonAlphaNumeric(summary);
    }

    public String toString(){
        String result;
        return commentId + " is JavaDoc: "+ isJavadoc + "\tSimilarity: "+ jaccardSimilarity +"\t"+cosineSimilarity;
    }


}
