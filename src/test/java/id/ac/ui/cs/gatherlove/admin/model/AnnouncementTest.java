package id.ac.ui.cs.gatherlove.admin.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnnouncementTest {

    @Test
    void testAnnouncementCreation() {
        Announcement announcement = new Announcement();
        announcement.setId(UUID.randomUUID());
        announcement.setTitle("Pengumuman Penting");
        announcement.setContent("Ini adalah isi pengumuman penting");
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setStatus(Announcement.AnnouncementStatus.ACTIVE);

        assertNotNull(announcement.getId());
        assertEquals("Pengumuman Penting", announcement.getTitle());
        assertEquals("Ini adalah isi pengumuman penting", announcement.getContent());
        assertNotNull(announcement.getCreatedAt());
        assertEquals(Announcement.AnnouncementStatus.ACTIVE, announcement.getStatus());
    }

    @Test
    void testPrePersist() {
        Announcement announcement = new Announcement();
        announcement.setTitle("Pengumuman Baru");
        announcement.setContent("Konten pengumuman");

        // Trigger prePersist
        announcement.onCreate();

        assertNotNull(announcement.getCreatedAt());
        assertEquals(Announcement.AnnouncementStatus.ACTIVE, announcement.getStatus());
    }
}