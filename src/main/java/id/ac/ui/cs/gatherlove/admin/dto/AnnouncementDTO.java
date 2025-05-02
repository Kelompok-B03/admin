package id.ac.ui.cs.gatherlove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String status;
}