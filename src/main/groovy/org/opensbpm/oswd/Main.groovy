package org.opensbpm.oswd

import org.apache.commons.cli.*

class Main {
    private static final Option INPUT_OPTION = Option.builder("input")
            .argName("file")
            .hasArg()
            .desc("use given oswd file")
            .build()
    private static final Option OUTPUT_OPTION = Option.builder("output")
            .argName("file")
            .hasArg()
            .desc("use given output file")
            .build()
    private static final Option HELP_OPTION = new Option("help", "print this message")

    static void main(String[] args) throws Exception {
        Options options = new Options()

        options.addOption(INPUT_OPTION)
        options.addOption(OUTPUT_OPTION)
        options.addOption(HELP_OPTION)

        try {
            // parse the command line arguments
            CliConfiguration cliConfiguration = new CliConfiguration(new DefaultParser().parse(options, args))
            if (cliConfiguration.hasHelp()) {
                printHelp(options)
            } else {
                def inputFile = cliConfiguration.getInputFile()
                def outputFile = cliConfiguration.getOutputFile()

                new OswdConverter(inputFile, outputFile).convert()

                System.out.println("Converted " + inputFile + " to " + outputFile)
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println(exp.getMessage())
            printHelp(options)
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = HelpFormatter.builder()
                .setPrintWriter(new PrintWriter(System.err))
                .get()
        formatter.printHelp("oswd", options)
    }


    private static class CliConfiguration {
        private final CommandLine line

        CliConfiguration(CommandLine line) {
            this.line = line
        }

        boolean hasHelp() {
            return line.hasOption(HELP_OPTION)
        }

        File getInputFile() throws ParseException {
            if (line.hasOption(INPUT_OPTION)) {
                File inputFile = new File(line.getOptionValue(INPUT_OPTION))
                if (!inputFile.exists()) {
                    throw new MissingOptionException("Input file does not exist")
                }
                return inputFile
            } else {
                throw new MissingOptionException("Input file is missing")
            }
        }

        File getOutputFile() throws ParseException {
            if (line.hasOption(OUTPUT_OPTION)) {
                return new File(line.getOptionValue(OUTPUT_OPTION))
            } else {
                throw new MissingOptionException("Output file is missing")
            }
        }

    }
}
