package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.AnnouncementDTO;

import java.util.List;
import java.util.UUID;

public interface AnnouncementService {
    List<AnnouncementDTO> getAllAnnouncements();
    AnnouncementDTO createAnnouncement(AnnouncementDTO announcementDTO);
    boolean deleteAnnouncement(UUID id);
}