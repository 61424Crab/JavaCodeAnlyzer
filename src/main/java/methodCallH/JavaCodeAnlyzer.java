package methodCallH;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.CompilationUnit.Storage;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.SourceRoot;

public class JavaCodeAnlyzer {
	
	
	private static String PROJECT_PATH = "path/to/your/project";
	private static Path BASE_PATH = Paths.get(PROJECT_PATH);
	private static List<CompilationUnit> compilationUnits;
	
	public static void main(String[] args) throws IOException  {
		convertJavaFilesToUTF8(BASE_PATH);
		SourceRoot sourceRoot = new SourceRoot(Paths.get(PROJECT_PATH));
		List<ParseResult<CompilationUnit>> paresResults = sourceRoot.tryToParse();
		
		compilationUnits = paresResults.stream()
				                       .filter(ParseResult::isSuccessful)
				                       .map(ParseResult::getResult)
				                       .filter(Optional::isPresent)
				                       .map(Optional::get)
				                       .collect(Collectors.toList());
		
		for (CompilationUnit cu : compilationUnits) {
			processCompilationUnit(cu);
		}
	}
	
	private static void convertJavaFilesToUTF8(Path path) throws IOException {
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
	
	private static void processCompilationUnit(CompilationUnit cu) {
		  String relativePath = cu.getStorage()
                  .map(Storage::getPath)
                  .map(p -> BASE_PATH.relativize(p).toString())
                  .orElse("Unknow File");
		  Path completePath = cu.getStorage().map(Storage::getPath).orElse(null);
		  if (completePath == null) return;
		  String directoryPath = BASE_PATH.relativize(completePath.getParent()).toString();
		  String fileName = completePath.getFileName().toString();
	
		  
		  cu.findAll(MethodDeclaration.class)
		    .stream()
		    .filter(MethodDeclaration::isPublic)
		    .forEach(m -> {
		    	System.out.println("File: " + relativePath + " - Publice method found: " + m.getSignature());
		    	System.out.println(directoryPath);
		    	System.out.println("\t" + fileName);
		    	findMethodUsagesInAllFiles(m);
		    });
	}
	
	private static void findMethodUsagesInAllFiles(MethodDeclaration method) {
		List<String> usages = new ArrayList<>();
		
		for (CompilationUnit cu : compilationUnits) {
			
			Path filePath = cu.getStorage().map(Storage::getPath).orElse(null);
			if (filePath == null) continue;
			List<String> lines;
	        try {
	            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
	        } catch (IOException e) {
	            e.printStackTrace();
	            continue;
	        }
	        
	        final List<String> effectiveLines = lines; 
			
			cu.findAll(MethodCallExpr.class).stream()
			  .filter(mce -> mce.getNameAsString().equals(method.getNameAsString()))
			  .forEach(mce -> {
				  int lineNumber = mce.getRange().map(range -> range.begin.line).orElse(-1);
				  String lineContent = lineNumber != -1? effectiveLines.get(lineNumber - 1).trim() : "Unknow line Content";
				  String relativePath = cu.getStorage()
						                  .map(Storage::getPath)
						                  .map(p -> BASE_PATH.relativize(p).toString())
						                  .orElse("Unknow File");
						                  
				  usages.add("\t\tCalled in file: " + relativePath + " at line: " + lineNumber + "LineContent: " + lineContent);
			  });
		}
		if (!usages.isEmpty()) {
			System.out.println("\t\tMethod " + method.getName() + " has the following usages: ");
			for (String usage : usages) {
				System.out.println(usage);
			}
		}
	}
	
}
