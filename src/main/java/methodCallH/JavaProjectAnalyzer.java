package methodCallH;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.SourceRoot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaProjectAnalyzer {

	private static final String PROJECT_PATH = "H:\\DevelopTools\\workspace\\HybridEncryptionWithSignature\\HybridEncryptionWithSignature";
	private static final String OUTPUT_DIRECTORY = "H:\\DevelopTools\\test";

	private static final Path BASE_PATH = Paths.get(PROJECT_PATH);
	private static List<CompilationUnit> compilationUnits;
	private static int counter = 1;

	public static void main(String[] args) throws IOException {
	    convertJavaFilesToUTF8(BASE_PATH);
	    SourceRoot sourceRoot = new SourceRoot(BASE_PATH);
	    List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();

	    compilationUnits = parseResults.stream()
	            .map(ParseResult::getResult)
	            .filter(Optional::isPresent)
	            .map(Optional::get)
	            .collect(Collectors.toList());

	    printDirectoryTree(BASE_PATH.toFile(), "");

	    for (CompilationUnit cu : compilationUnits) {
	        processCompilationUnit(cu);
	    }
	}

	public static void convertJavaFilesToUTF8(Path path) throws IOException {
		Files.walk(path).filter(p -> p.toString().endsWith(".java")).forEach(p -> {
			try {
				byte[] encoded = Files.readAllBytes(p);
				String content = new String(encoded, Charset.forName("Shift-JIS"));
				Files.write(p, content.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static void printDirectoryTree(File node, String indent) {
	    if (node.isDirectory()) {
	        if (containsJavaFile(node)) {
	            System.out.println(indent + node.getName());
	            for (File child : node.listFiles()) {
	                printDirectoryTree(child, indent + "   ");
	            }
	        }
	    } else if (node.getName().endsWith(".java")) {
	        System.out.println(indent + node.getName());
	    }
	}
	
	public static void processCompilationUnit(CompilationUnit cu) {
	    cu.getStorage().ifPresent(storage -> {
	        String relativePath = BASE_PATH.relativize(storage.getPath()).toString();
	        
	    });

	    // Retrieve all public methods first
	    List<MethodDeclaration> publicMethods = cu.findAll(MethodDeclaration.class).stream().filter(MethodDeclaration::isPublic).collect(Collectors.toList());
	    
	    for (MethodDeclaration method : publicMethods) {
	        System.out.println(indent(2) + "Usages of method: " + method.getNameAsString());
	        findUsagesInProject(method.getNameAsString(), cu);
	    }

	    // Handle Constructors separately
	    List<ConstructorDeclaration> constructors = cu.findAll(ConstructorDeclaration.class);
	    for (ConstructorDeclaration constructor : constructors) {
	        System.out.println(indent(2) + "Usages of constructor: " + constructor.getNameAsString());
	        findUsagesInProject(constructor.getNameAsString(), cu);
	    }
	}
	
	public static void findUsagesInProject(String methodName, CompilationUnit cu) {
	    for (CompilationUnit otherCU : compilationUnits) {
	        otherCU.findAll(MethodCallExpr.class).stream().filter(mce -> mce.getNameAsString().equals(methodName))
	        .forEach(mce -> {
	            String relativePath = otherCU.getStorage().map(CompilationUnit.Storage::getPath)
	            .map(p -> BASE_PATH.relativize(p).toString()).orElse("Unknown File");
	            int lineNumber = mce.getRange().map(range -> range.begin.line).orElse(-1);
	            String output = indent(3) + relativePath + ":" + lineNumber + ":" + methodName + "()";
	            System.out.println(output);
	        });
	    }
	}
	
	public static String indent(int level) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < level; i++) {
	        sb.append("   ");
	    }
	    return sb.toString();
	}

	public static void recordMethodUsage(String methodName, CompilationUnit cu) {
		counter++; // Increment the counter when a new method is discovered.

		Path filePath = cu.getStorage().map(CompilationUnit.Storage::getPath).orElse(null);
		if (filePath == null)
			return;

		List<String> effectiveLines;
		try {
			effectiveLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		cu.findAll(MethodCallExpr.class).stream().filter(mce -> mce.getNameAsString().equals(methodName))
				.forEach(mce -> {
					int lineNumber = mce.getRange().map(range -> range.begin.line).orElse(-1);
					String lineContent = (lineNumber != -1 && lineNumber <= effectiveLines.size())
							? effectiveLines.get(lineNumber - 1).trim()
							: "Unknown line content";
					String relativePath = cu.getStorage().map(CompilationUnit.Storage::getPath)
							.map(p -> BASE_PATH.relativize(p).toString()).orElse("Unknown File");
					String output = relativePath + ":" + lineNumber + ":" + lineContent;
					System.out.println(output);

					// Save to file
					saveToFile(methodName, cu, output);
				});
	}

	public static void saveToFile(String methodName, CompilationUnit cu, String content) {
		String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString().replace('.', '_'))
				.orElse("DefaultPackage");
		String className = cu.getPrimaryTypeName().orElse("UnknownClass");
		Path directoryPath = Paths.get(OUTPUT_DIRECTORY, packageName);
		try {
			Files.createDirectories(directoryPath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String filename = String.format("%05d", counter) + "_" + className + "_" + methodName + ".md";
		Path filePath = directoryPath.resolve(filename);

		try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
			writer.write(content);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean containsJavaFile(File directory) {
	    if (directory.isDirectory()) {
	        for (File child : directory.listFiles()) {
	            if (child.getName().endsWith(".java") || containsJavaFile(child)) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
}
