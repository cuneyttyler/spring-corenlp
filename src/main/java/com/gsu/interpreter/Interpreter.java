package com.gsu.interpreter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interpreter {
    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.err.println("Not enough arguments");
            return;
        }

        String rootFile = args[0].split("\\.").length == 0 ? args[0] : args[0].split("\\.")[0];
        File root = new File(rootFile);

        if (root == null) {
            System.err.println("There is no such package.");
            return;
        }

        String basePath = root.getAbsoluteFile().getParentFile().getAbsolutePath();

        List<File> files = getFilesUnder(root);
        List<JavaFile> javaFiles = getJavaFiles(files, root.getAbsoluteFile().getParentFile());
        JavaPackage rootPackage = byPackage(javaFiles);

        createParentFolder(basePath);
        createFolders(rootPackage, basePath);
        traverseAndProcess(rootPackage, basePath);

    }

    public static void traverseAndProcess(JavaPackage root, String base) throws Exception {

        for (JavaFile jf : root.getJavaFiles()) {
            File file = new File(jf.getAbsolutePath());

            String contents = FileUtils.readFileToString(file, "UTF-8");
            String[] lines = contents.split(System.getProperty("line.separator"));

            // HERE is where the processing/compiling occurs
            List<String> parsed = new Parser().parse(contents);

            // write to file
            writeToFile(base + "/compiled/" + jf.getRelativePath(),parsed);

        }

        for (JavaPackage jp : root.getJavaPackages()) {
            traverseAndProcess(jp, base);
        }
    }

    public static void writeToFile(String path, List<String> lines) throws Exception {
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        for (String line : lines) {
            writer.println(line);
        }

        writer.close();
    }

    public static void createParentFolder(String base) {
        File f = new File(base + "/compiled/");

        if (f.mkdir()) {
//            System.out.println("directory was created successfully");
        } else {
//            System.out.println("failed trying to create the directory");
        }
    }

    public static void createFolders(JavaPackage root, String base) {
        File f = new File(base + "/compiled/" + root.getName().replaceAll("\\.", "/"));

        if (f.mkdir()) {
//            System.out.println("directory was created successfully");
        } else {
//            System.out.println("failed trying to create the directory");
        }

        for (JavaPackage jp : root.getJavaPackages()) {
            createFolders(jp, base);
        }
    }

    public void read(String filename) {
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static List<File> getFilesUnder(File root) {
        List<File> folders = new ArrayList<File>();

        for (File subFolder : root.listFiles()) {
            if (subFolder.isDirectory()) {
                folders.addAll(getFilesUnder(subFolder));
            } else if (FilenameUtils.getExtension(subFolder.getAbsolutePath()).equals("java")) {
                folders.add(subFolder);
            }
        }

        return folders;
    }

    public static List<JavaFile> getJavaFiles(List<File> files, File base) {
        List<JavaFile> javaFiles = new ArrayList<JavaFile>();

        for (File file : files) {
            JavaFile jf = new JavaFile();
            jf.setAbsolutePath(file.getAbsolutePath());
            jf.setFileName(file.getName());
            jf.setPackageName(getPackageName(getRelativePath(file, base)));
            jf.setRelativePath(getRelativePath(file, base));

            javaFiles.add(jf);

        }

        return javaFiles;
    }

    public static JavaPackage byPackage(List<JavaFile> files) {
        HashMap<String, JavaPackage> packages = new HashMap<>();
        for (JavaFile jf : files) {
            if (packages.get(jf.getPackageName()) == null) {
                JavaPackage jp = new JavaPackage();
                jp.setName(jf.getPackageName());
                jp.getJavaFiles().add(jf);

                packages.put(jp.name, jp);
            } else {
                packages.get(jf.getPackageName()).getJavaFiles().add(jf);
            }
        }

        HashMap<String, Boolean> isProcessed = new HashMap<>();
        for (int i = 0; i < packages.keySet().size(); i++) {
            String packageName = (String) packages.keySet().toArray()[i];
            JavaPackage jp = packages.get(packageName);

            if (isProcessed.get(packageName) == null) {
                String parentName = getParentName(packageName);
                String result = null;
                for (String searchParent : packages.keySet()) {
                    if (searchParent.equals(parentName)) {
                        result = searchParent;
                    } else {
                        // do nothing
                    }
                }

                if (result != null) {
                    JavaPackage parent = packages.get(result);
                    parent.getJavaPackages().add(jp);
                } else {
                    // this package has no java file under itself, we need to create it
                    JavaPackage parent = new JavaPackage();
                    parent.setName(parentName);
                    parent.getJavaPackages().add(jp);

                    packages.put(parentName, parent);
                }

                isProcessed.put(packageName, true);
            } else {
                // do nothing
            }


        }

        String rootPackageName = files.get(0).getPackageName().split("\\.")[0];
        return packages.get(rootPackageName);
    }

    public static String getRelativePath(File file, File base) {
        if (file == null || base == null) return null;

        return base.toURI().relativize(file.toURI()).getPath();
    }

    public static String getPackageName(String relativePath) {
        if (relativePath.split("/").length == 0) {
            return relativePath;
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < relativePath.split("/").length - 1; i++) {
                stringBuilder.append(relativePath.split("/")[i]);
                if (i < relativePath.split("/").length - 2) {
                    stringBuilder.append(".");
                }
            }

            return stringBuilder.toString();
        }

    }

    public static String getParentName(String name) {
        if (name.split("\\.").length == 0) {
            return "*";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < name.split("\\.").length - 1; i++) {
                stringBuilder.append(name.split("\\.")[i]);

                if (i < name.split("\\.").length - 2) {
                    stringBuilder.append(".");
                }
            }

            return stringBuilder.toString();
        }
    }
}
