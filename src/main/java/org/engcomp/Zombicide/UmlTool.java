package org.engcomp.Zombicide;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.io.File;
import java.io.IOException;


public class UmlTool {
    public static void main(String[] args) throws IOException {
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("org.engcomp")
                .scan()) {
            scanResult.getAllClasses()
                    .generateGraphVizDotFile(new File("uml.dot"));
        }
    }
}
