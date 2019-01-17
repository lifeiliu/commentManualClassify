import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SourceFileUtil {


    public static List<CommentForCat> getCommentsFromFile(File sourceCodeFile) throws Exception {
        List<CommentForCat> result = new LinkedList<>();
        List<FunctionMap> functionMaps = getFunctionMapFormFile(sourceCodeFile);
        /*  //test
            functionMaps.forEach(e->{
            System.out.println(e.functionName);
            System.out.println(e.className);
            System.out.println("Start "+e.startLine + " End: "+e.endLine);
            System.out.println(e.comments);
        });*/

        if (!verifySourceCode(sourceCodeFile)) throw new Exception("not java file");
        try {
            CompilationUnit cu = JavaParser.parse(sourceCodeFile);
            List<Comment> comments = cu.getComments();
            List<Comment> combinedComments = combineMultiLineComment(comments);
            for (Comment comment : combinedComments){
                CommentForCat newCommentToResult = CommentForCat.convertFromComment(comment);
                for (FunctionMap map: functionMaps){
                    if (map.comments.contains(comment)){
                        newCommentToResult.methodName = map.functionName;
                        newCommentToResult.className = map.className;
                    }

                }
                result.add(newCommentToResult);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (CommentForCat comment : result){
            comment.fileName = sourceCodeFile.getName();

        }
        return result;

    }

    public static List<FunctionMap> getFunctionMapFormFile(File sourceCodeFile) throws Exception{
        List<FunctionMap> results = new ArrayList<>();
        if (!verifySourceCode(sourceCodeFile)) throw new Exception("not java file");
        CompilationUnit cu = JavaParser.parse(sourceCodeFile);
        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
        List<ClassMap> classMaps = new ArrayList<>();
        for (ClassOrInterfaceDeclaration clazz : classes){
            ClassMap classMap = ClassMap.getClassMap(clazz);
            classMaps.add(classMap);
        }
        for (MethodDeclaration method : methodDeclarations){
            FunctionMap functionMap = FunctionMap.createFunctionMap(method);
            functionMap.fileName = sourceCodeFile.getName();
            for(ClassMap classMap : classMaps){
                if (classMap.methods.contains(method)){
                    functionMap.className = classMap.className;
                }
            }
            results.add(functionMap);

        }
        return results;
    }



    private static boolean verifySourceCode(File sourceCodeFile){
        String fileName = sourceCodeFile.getName();

        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            return "java".equals(fileName.substring(fileName.lastIndexOf("." )+ 1));
        }
        return false;


    }

    private static List combineMultiLineComment (List<Comment> comments){
        comments.sort(new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getRange().get().begin.line - o2.getRange().get().begin.line;
            }
        });
        if (comments.size() < 1) return comments;
        for (int i = 1; i < comments.size() - 1; i++){

            if ((comments.get(i).isOrphan() || comments.get(i-1).isOrphan()) && comments.get(i-1).getEnd().get().line
                    == comments.get(i).getBegin().get().line - 1 ) {
                comments.get(i).setContent(comments.get(i - 1).getContent() +" "+ comments.get(i).getContent());
                comments.remove(i - 1);
                i--;
            }
        }
        return comments;
    }



    /*public static void main(String[] args){
      File file = new File("/home/ggff/Desktop/sourceCode/crawl4j/HtmlContentHandler.java");
        try {
            List<FunctionMap> map = getFunctionMapFormFile(file);
            for(FunctionMap function : map){
                System.out.println(function.functionName);
                System.out.println(function.comments);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
