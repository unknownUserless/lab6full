package client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringJoiner;

public class FileWorker {

    private File file;

    public FileWorker(File file) {
        this.file = file;
    }

    public String getFileContent() {
        StringJoiner joiner = new StringJoiner("\n");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                joiner.add(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return joiner.toString();
    }

    public void saveFileContent(String fileContent){
        try(FileWriter writer = new FileWriter(file)){
            writer.append(fileContent);
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
