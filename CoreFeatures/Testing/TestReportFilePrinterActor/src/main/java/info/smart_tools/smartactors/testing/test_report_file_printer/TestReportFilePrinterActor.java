package info.smart_tools.smartactors.testing.test_report_file_printer;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.testing.test_report_file_printer.exception.FilePrintReportActorException;
import info.smart_tools.smartactors.testing.test_report_file_printer.wrapper.PrintWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TestReportFilePrinterActor {
    private final String rootPath;

    /**
     * @param rootPath is a path to a directory where reports should be saved in.
     */
    public TestReportFilePrinterActor(final String rootPath) {
        this.rootPath = rootPath;
    }

    public void print(final PrintWrapper wrapper) throws FilePrintReportActorException {
        try {
            final Path path = Paths.get(rootPath, wrapper.getReportName());
            final InputStream is = new ByteArrayInputStream(wrapper.getReport().getBytes());
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FilePrintReportActorException("Can't write report to file: " + e.getMessage(), e);
        } catch (ReadValueException e) {
            throw new FilePrintReportActorException("Can't read values from wrapper: " + e.getMessage(), e);
        }
    }
}