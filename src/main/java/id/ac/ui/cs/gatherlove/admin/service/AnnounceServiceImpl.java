package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.AnnouncementDTO;
import id.ac.ui.cs.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.gatherlove.admin.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public List<AnnouncementDTO> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAllByOrderByCreatedAtDesc();
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementDTO createAnnouncement(AnnouncementDTO announcementDTO) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDTO.getTitle());
        announcement.setContent(announcementDTO.getContent());

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        return convertToDTO(savedAnnouncement);
    }

    @Override
    public boolean deleteAnnouncement(UUID id) {
        Optional<Announcement> announcement = announcementRepository.findById(id);
        if (announcement.isPresent()) {
            announcementRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private AnnouncementDTO convertToDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setStatus(announcement.getStatus().name());
        return dto;
    }
}