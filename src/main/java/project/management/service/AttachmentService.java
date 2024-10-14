package project.management.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.management.model.Project;
import project.management.model.Task;
import project.management.model.Work;
import project.management.model.attachment.Attachment;
import project.management.model.attachment.ProjectAttachment;
import project.management.model.attachment.TaskAttachment;
import project.management.model.attachment.WorkAttachment;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Getter
@Slf4j
public class AttachmentService {
    @Value("${file.upload-dir}")
    private String folderDirection;

    public <T, K extends Attachment> List<K> addAttachments(List<MultipartFile> files, Long id, String type, T attachable) {
        List<K> attachments = new ArrayList<>();
        Path directoryPath = Paths.get(folderDirection, type, String.valueOf(id));
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                log.info("Directory created: {}", directoryPath);
            } else {
                log.warn("Directory already exists for {} ID {}: {}",
                        type, id, directoryPath);
            }

            try (DirectoryStream<Path> directoryStream = Files
                    .newDirectoryStream(directoryPath)) {
                for (Path oldFile : directoryStream) {
                    deleteAttachment(oldFile);
                }
            }
            if(files != null) {
                for (MultipartFile file : files) {
                    Path filePath = directoryPath.resolve(Objects
                            .requireNonNull(file.getOriginalFilename()));
                    file.transferTo(filePath);
                    log.info("Added file {} to {}", filePath, directoryPath);
                    attachments.add(getAttachment(file, type, directoryPath.toString(), attachable));
                }
            }
        } catch (IOException e) {
            log.error("File could not be transferred {}", e.getMessage(), e);
        }
        return attachments;
    }

    @SuppressWarnings("unchecked")
    private <T, K extends Attachment> K getAttachment(MultipartFile file, String type, String folderDirection, T attachable) {
        return switch (type) {
            case "project" -> (K) ProjectAttachment.builder()
                    .filename(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .filePath(folderDirection)
                    .project((Project) attachable)
                    .build();
            case "task" -> (K) TaskAttachment.builder()
                    .filename(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .filePath(folderDirection)
                    .task((Task) attachable)
                    .build();
            case "work" -> (K) WorkAttachment.builder()
                    .filename(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .filePath(folderDirection)
                    .work((Work) attachable)
                    .build();
            default -> throw new RuntimeException("Unsupported attachment type");
        };
    }

    public void deleteAttachment(Path path) {
        try {
            if (Files.exists(path) && Files.isRegularFile(path)) {
                Files.delete(path);
                log.info("Deleted file: {}", path);
            }
        } catch (IOException e) {
            log.error("File {} could not be deleted because of {}",
                    path, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteFolder(Path path) {
        try {
            if(Files.exists(path) && Files.isDirectory(path)) {
                try(Stream<Path> stream = Files.walk(path)) {
                        stream.sorted(Comparator.reverseOrder())
                            .forEach(this::deleteAttachment);
                }
                Files.delete(path);
            }
        } catch (IOException e) {
            log.error("Folder {} could not be deleted because of {}",
                    path, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}