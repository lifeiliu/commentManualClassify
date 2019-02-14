import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PreprocessingDocComment {

    String commentID ;
    boolean isPublic;
    CommentLocation location;
    double jaccardSimilarity;
    double cosineSimilarity;
    int lengthOfDescription;
    int numOfAttribute;
    boolean isAuthorOnly;

    EnumSet<CommentLocation> outMethod = EnumSet.of(CommentLocation.ClassTopComment,
                                                    CommentLocation.MethodTopComment,
                                                    CommentLocation.ClassFieldComment);

    public PreprocessingDocComment(String commentID, boolean isPublic, CommentLocation location,
                                   double jaccardSimilarity, double cosineSimilarity, int lengthOfDescription,
                                   int numOfAttribute, boolean isAuthorOnly) {
        this.commentID = commentID;
        this.isPublic = isPublic;
        this.location = location;
        this.jaccardSimilarity = jaccardSimilarity;
        this.cosineSimilarity = cosineSimilarity;
        this.lengthOfDescription = lengthOfDescription;
        this.numOfAttribute = numOfAttribute;
        this.isAuthorOnly = isAuthorOnly;
    }

    public PreprocessingDocComment Preprocessing(CommentForCat comment){
        String commentID = getCommentID(comment);
        CommentLocation location = getLocation(comment);
        double jaccardSimilarity = getJaccardSimilarity(comment);
        double cosineSimilarity = getCosineSimilarity();
        int lengthOfDescription = getLengthOfDescription(comment);
        int numOfAttribute = getNumOfAttribute(comment);
        boolean isPublic = isPublic(comment);
        boolean isAuthorOnly = isAuthorOnly(comment);

        return new PreprocessingDocComment(commentID, isPublic, location, jaccardSimilarity, cosineSimilarity
                                            ,lengthOfDescription, numOfAttribute,isAuthorOnly);


    }

    public String getCommentID(CommentForCat comment) {
        return comment.fileName + "_" + comment.lineStartNumber;
    }

    public boolean isPublic(CommentForCat comment) {
        if(outMethod.contains(comment.commentLocation) && comment.commentedCode.startsWith("public")){
            return true;
        }else {
            return false;
        }
    }

    public CommentLocation getLocation(CommentForCat comment) {
        return comment.commentLocation;
    }

    public double getJaccardSimilarity(CommentForCat comment) {
        if (comment.methodSignature.equals("") || comment.text.equals("")){
            return 0;
        }
        String docmentCommentDescription =WordsUtil.getDocCommentDestcription(comment.text);
        List<String> filteredCommentWords = WordsUtil.filterStopWords(WordsUtil.splitSentenceAndCamelWord(docmentCommentDescription),
                                                                    WordsUtil.generateStopWords());
        String processedCommentText = "";
        for (String word : filteredCommentWords){
            processedCommentText += word;
        }

        Similarity similarity = new Similarity();
        return similarity.jaccardSimilarity(comment.methodSignature,processedCommentText);
    }

    public double getCosineSimilarity() {
        return cosineSimilarity;
    }

    public String commentDescription(CommentForCat comment){
        return WordsUtil.getDocCommentDestcription(comment.text);
    }

    public int getLengthOfDescription(CommentForCat comment) {
        return commentDescription(comment).length();
    }

    public int getNumOfAttribute(CommentForCat comment) {
        return comment.text.split("@").length;
    }

    public boolean isAuthorOnly(CommentForCat comment) {
        if (getNumOfAttribute(comment) == 1 && comment.text.contains("@author") && getLengthOfDescription(comment) == 0){
            return true ;
        }
        return false;
    }


}
