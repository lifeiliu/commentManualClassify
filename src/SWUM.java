import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
    SWUM stands for Software Word Usage Model
    This model is used to generate code summary
    I used part of its idea of getting the most important statement in method body
    which can keep the meaning of the method

    idea get from a paper "Towards Automatically Generating
            Summary Comments for Java Methods"

 */
public class SWUM {
    public static Set<String> generateMethodSummary(MethodDeclaration md){
        Set <String> methodSummary = new HashSet<>();

        Set<Statement> sUnits = new HashSet<>();
        if(md.getBody().isPresent()){
            BlockStmt methodBody = md.getBody().get();
            sUnits.addAll(endingSUnit(methodBody));
            sUnits.addAll(voidReturnSUnit(methodBody));
            sUnits.addAll(dataFacilitatingSUnit(sUnits,methodBody));
            for(Statement each : sUnits){
                methodSummary.addAll(WordsUtil.splitSentenceAndCamelWord(each.toString()));
            }
        }

        return methodSummary;
    }


    private static List<Statement> endingSUnit(BlockStmt methodBody){
        List<Statement> result = new ArrayList<>();

        NodeList<Statement> statements = methodBody.getStatements();

        for(Statement each : statements){
            if(each.isReturnStmt() && !each.toString().contains("null")){
                each.removeComment();
                result.add(each);
            }
        }

        result.add(statements.get(statements.size() - 1));

        return result;

    }

    /*An s unit which has a method call
    that does not return a value or whose return value is not as-
    signed to a variable is a void-return s unit.*/
    private static Set<Statement> voidReturnSUnit(BlockStmt methodBody){
        Set<Statement> result = new HashSet<>();
        List<Statement> methodCalls = new ArrayList<>();
        new MethodCallVisitor().visit(methodBody,methodCalls);

        for(Statement s : methodCalls){
            if (s.findAll(MethodCallExpr.class).contains("="))
                methodCalls.remove(s);
        }
        result.addAll(methodCalls);
        return result;

    }





    private static Set<Statement>  dataFacilitatingSUnit(Set<Statement> SUnits, BlockStmt
                                                           methodBoby){
        Set<String> variableNames = new HashSet<>();
        Set<Statement> result = new HashSet<>();
        for(Statement s : SUnits){
            if (s instanceof ReturnStmt){
                if (((ReturnStmt) s).getExpression().isPresent()){
                    Expression expression = ((ReturnStmt) s).getExpression().get();
                    if (expression instanceof NameExpr){
                        variableNames.add(((NameExpr) expression).getNameAsString());
                    }
                }
            }
            s.getChildNodes().forEach(e-> {
                if (e instanceof MethodCallExpr){
                    ((MethodCallExpr) e).getArguments().forEach(a -> {
                        if(a instanceof NameExpr){
                            variableNames.add(((NameExpr) a).getNameAsString());
                        }
                    });
                }
            });

        }

        //System.out.println(variableNames);
        Set<Statement> assignmentStatement = new HashSet<>();

        new AssignmentVisitor().visit(methodBoby,assignmentStatement);
        System.out.println("Assginment statement:" + assignmentStatement);

        for (Statement s : methodBoby.getStatements()){
            if(assignmentStatement.contains(s)){
                for (String name: variableNames){
                    if (s.toString().contains(name))
                        result.add(s);
                }
            }

        }

        return result;

    }


    private static class MethodCallVisitor extends VoidVisitorAdapter<List<Statement>>{
        @Override
        public void visit(MethodCallExpr n, List<Statement> arg) {
            super.visit(n, arg);
            if(n.getParentNode().isPresent()){
                Node parent = n.getParentNode().get();
                if(parent instanceof Statement){
                    arg.add((Statement) parent.removeComment());
                    //System.out.println(parent);
                }
            }

        }
    }

    private static class AssignmentVisitor extends VoidVisitorAdapter<Set<Statement>>{
        @Override
        public void visit(AssignExpr n, Set<Statement> collector) {
            super.visit(n,collector);

            if (n.getParentNode().isPresent()){
                Node parenet = n.getParentNode().get();
                if (parenet instanceof Statement){
                    collector.add((Statement) parenet);
                }
            }



        }
    }

    private static class ReturnStmtVisitor extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(ReturnStmt n, List<String> arg) {
            super.visit(n, arg);
            String statement = n.toString().trim();
            if(!statement.contains("null")){
                arg.add(statement.substring(7));
            }
        }
    }



    public static void main(String[] args) throws FileNotFoundException {
        String path ="/home/ggff/workplace/maven/maven/maven-core/src/main/java/org/apache/maven/settings/SettingsUtils.java";
        File sourceFile = new File(path);
        CompilationUnit cu = JavaParser.parse(sourceFile);
        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
        /*MethodDeclaration aMethod = methodDeclarations.get(methodDeclarations.size()-3);
        System.out.println(aMethod);
        List<String> returnAvariables = new ArrayList<>();
        new ReturnStmtVisitor().visit(aMethod,returnAvariables);
        System.out.println(returnAvariables);*/
        List<Statement> dataFacilitaing = new ArrayList<>();
        for(MethodDeclaration md:methodDeclarations){
            if(md.getBody().isPresent()){
                Set<Statement> sUnits = voidReturnSUnit(md.getBody().get());
                System.out.println(sUnits);
                dataFacilitaing.addAll(dataFacilitatingSUnit(sUnits,md.getBody().get()));

            }


            System.out.println(dataFacilitaing);
            dataFacilitaing.clear();
            System.out.println(md.toString());
            System.out.println(generateMethodSummary(md));
            System.out.println("\n");
        }



    }
}


