import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodUtil {

    private MethodDeclaration methodDeclaration;

    public MethodUtil(MethodDeclaration methodDeclaration){
        this.methodDeclaration = methodDeclaration;
    }


    public static boolean hasDocumentComment(MethodDeclaration md){
        if (md.getComment().isPresent()){
            return md.getComment().get().isJavadocComment();
        }
        return false;

    }

    public static Comment getDocumentComment(MethodDeclaration md){
        if(hasDocumentComment(md))
            return md.getComment().get();
        return null;
    }

    public static boolean isPublic(MethodDeclaration md){
        return md.hasModifier(Modifier.Keyword.PUBLIC);
    }

    public static Set<String> getSignatureWords(MethodDeclaration md){
        Set<String> signatureWords = new HashSet<>();

        String declaration = md.getDeclarationAsString(false,true,true);
        List<String> processed = WordsUtil.splitSentenceAndCamelWord(declaration);

        for (String s : processed){

            signatureWords.add(s);
        }
        return signatureWords;

    }

    public static Set<String> methodCodeSummary(MethodDeclaration md){
        Set<String> codeSummary = new HashSet<>();
        codeSummary.addAll(getSignatureWords(md));
        codeSummary.addAll(SWUM.generateMethodSummary(md));
        return codeSummary;
    }


    public static void main(String[] args) throws FileNotFoundException {
        File sourceFile = new File ("/home/ggff/workplace/maven/maven/maven-core/src/main/java/org/apache/maven/ReactorReader.java");
        CompilationUnit cu = JavaParser.parse(sourceFile);
        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
        List<MethodUtil> methodUtils = new ArrayList<>();
        for (MethodDeclaration md : methodDeclarations){
            System.out.println("=======================");
            System.out.println(getDocumentComment(md));
            System.out.println(getSignatureWords(md));
            System.out.println("\n");
        }

        MethodDeclaration last = methodDeclarations.get(methodDeclarations.size()-3);
        BlockStmt body = last.getBody().get();
        for(Statement eachline : body.getStatements()){
            if(!eachline.isReturnStmt() && !eachline.isExpressionStmt()){
                System.out.println(eachline);

            }
        }



    }

}

