package methodCallH;

import java.io.File;
import java.io.IOException;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class DirectoryTreePrinter {

    public static void main(String[] args) {
        File rootDir = new File("path/to/your/target/directory"); // 替换为你的目标目录路径
        printDirectoryTree(rootDir, "");
    }

    public static void printDirectoryTree(File node, String indent) {
        if (node == null) {
            return;
        }

        System.out.println(indent + "|-- " + node.getName());

        if (node.isDirectory()) {
            File[] files = node.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                printDirectoryTree(file, indent + "   ");
            }
        } else if (node.getName().endsWith(".java")) {
            // If it's a Java file, parse it and print constructors and public methods
            printJavaDetails(node);
        }
    }

    public static void printJavaDetails(File javaFile) {
    	try {
    		JavaParser parse = new JavaParser();
            CompilationUnit cu;
            
            
            cu = parse.parse(javaFile).getResult().orElse(null);
            
            // Print constructors
            for (ConstructorDeclaration constructor : cu.findAll(ConstructorDeclaration.class)) {
                System.out.println("   |-- Constructor: " + constructor.getSignature());
            }

            // Print public methods
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                if (method.isPublic()) {
                    System.out.println("   |-- Public method: " + method.getSignature());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}