package org.opensbpm.oswd;

import org.apache.commons.cli.*;
import org.opensbpm.engine.api.model.definition.ProcessDefinition;
import org.opensbpm.engine.xmlmodel.ProcessModel;
import org.opensbpm.oswd.convert.ProcessConverter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Main {
    private static Option INPUT_OPTION = Option.builder("input")
            .argName("file")
            .hasArg()
            .desc("use given oswd file")
            .build();
    private static Option OUTPUT_OPTION = Option.builder("output")
            .argName("file")
            .hasArg()
            .desc("use given output file")
            .build();
    private static final Option HELP_OPTION = new Option("help", "print this message");

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        options.addOption(INPUT_OPTION);
        options.addOption(OUTPUT_OPTION);
        options.addOption(HELP_OPTION);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            File inputFile;
            File outputFile;
            if (line.hasOption(INPUT_OPTION)) {
                inputFile = new File(line.getOptionValue(INPUT_OPTION));
                if (!inputFile.exists()) {
                    throw new MissingOptionException("Input file does not exist");
                }
            }else{
                throw new MissingOptionException("Input file is missing");
            }
            if (line.hasOption(OUTPUT_OPTION)) {
                outputFile = new File(line.getOptionValue(OUTPUT_OPTION));
            }else{
                throw new MissingOptionException("Output file is missing");
            }

            FileReader reader = new FileReader(inputFile);
            Process process = Oswd.parseOswd(reader);
            reader.close();

            ProcessDefinition processDefinition = new ProcessConverter().convert(process);
            FileWriter writer = new FileWriter(outputFile);
            new ProcessModel().marshal(processDefinition, writer);
            writer.close();

            if (line.hasOption(HELP_OPTION)) {
                printHelp(options);
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println(exp.getMessage());
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = HelpFormatter.builder()
                .setPrintWriter(new PrintWriter(System.err))
                .get();
        formatter.printHelp("oswd", options);
    }
}
