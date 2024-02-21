package com.ugasoft.xray_helper.test_plan_preparer;

import java.io.IOException;
import java.nio.file.*;

public class FileWatcher {
    private final Path path;

    public FileWatcher(Path path) {
        this.path = path;
    }

    public void watchFile() throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Watch service registered for: " + path);

        while (true) {
            WatchKey watchKey = watchService.take();
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY &&
                        event.context().toString().equals(path.getFileName().toString())) {
                    System.out.println("File " + path + " has changed!");
                    return;
                }
            }

            boolean valid = watchKey.reset();
            if (!valid) {
                break;
            }
        }
    }
}
