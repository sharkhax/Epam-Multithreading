package com.drobot.logistic_base.reader;

import com.drobot.logistic_base.exception.ReaderException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DataReader {

    private static final Logger LOGGER = LogManager.getLogger(DataReader.class);
    private static final String DEFAULT_FILE_PATH = "data/info.txt";

    public List<String> readFile() throws ReaderException {
        return readFile(DEFAULT_FILE_PATH);
    }

    public List<String> readFile(String filePath) throws ReaderException {
        List<String> result;
        try (FileReader reader = new FileReader(filePath)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            result = bufferedReader.lines().collect(Collectors.toList());
            LOGGER.log(Level.INFO, "File " + filePath + " has been read");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error while reading file", e);
            throw new ReaderException(e);
        }
        return result;
    }
}
