import java.util.Arrays;
import java.util.List;
import java.io.PrintWriter;
import java.io.IOException;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String[] nameAndFields = type.split(":");
            String className = nameAndFields[0].trim();
            String fields = nameAndFields[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(String.format("        R visit%s%s(%s %s);", typeName, baseName, typeName, baseName.toLowerCase()));
        }

        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(String.format("    static class %s extends %s {", className, baseName));

        writer.println(String.format("        %s(%s) {", className, fieldList));
        String[] fields = fieldList.split(",");
        for (String field : fields) {
            field = field.trim();
            String[] typeAndId = field.split(" ");
            writer.println(String.format("            this.%s = %s;", typeAndId[1], typeAndId[1]));
        }
        writer.println("        }");

        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println(String.format("            return visitor.visit%s%s(this);", className, baseName));
        writer.println("        }");
        writer.println();

        for (String field : fields) {
            writer.println("        final " + field.trim() + ";");
        }
        writer.println("    }");
    }
}
