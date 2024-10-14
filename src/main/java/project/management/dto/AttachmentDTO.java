package project.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class AttachmentDTO {
    private String filename;
    private String contentType;
}
