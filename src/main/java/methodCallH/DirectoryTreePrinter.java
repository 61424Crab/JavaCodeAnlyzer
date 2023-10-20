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

        if (node.isDirectory()) {
            File[] files = node.listFiles();
            if (files == null) {
                return;
            }

            boolean shouldPrintCurrentDirectory = anyChildIsTargetDirectory(node);

            if (shouldPrintCurrentDirectory) {
                System.out.println(indent + "|-- " + node.getName());
            }

            for (File file : files) {
                printDirectoryTree(file, indent + "   ");
            }
        } else if (node.getName().endsWith(".java") && isUnderTargetDirectory(node)) {
            System.out.println(indent + "|-- " + node.getName());
            printJavaDetails(node);
        }
    }
    
    
    
    public static boolean isTargetDirectory(File file) {
        return file.isDirectory() && (file.getName().equals("AAA") || file.getName().equals("AAB") || file.getName().equals("AAC"));
    }

    public static boolean isUnderTargetDirectory(File file) {
        File parent = file.getParentFile();
        return parent != null && isTargetDirectory(parent);
    }

    public static boolean anyChildIsTargetDirectory(File directory) {
        if (!directory.isDirectory()) {
            return false;
        }
        File[] children = directory.listFiles();
        if (children == null) {
            return false;
        }
        for (File child : children) {
            if (isTargetDirectory(child) || anyChildIsTargetDirectory(child)) {
                return true;
            }
        }
        return false;
    }
    
    public static void printJavaDetails(File javaFile) {
    	try {
    		JavaParser parse = new JavaParser();
            CompilationUnit cu;
            
            
            cu = parse.parse(javaFile).getResult().orElse(null);
            
            if(cu != null ) {
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
            }
                        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
