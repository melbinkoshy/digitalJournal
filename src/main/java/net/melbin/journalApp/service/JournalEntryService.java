package net.melbin.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.melbin.journalApp.entity.JournalEntry;
import net.melbin.journalApp.entity.User;
import net.melbin.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try{
            User user = userService.findUserByUsername(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        }
        catch(Exception e){
            log.error("Error saving journal entry", e);
            throw new RuntimeException("Error saving journal entry");
        }

    }

    public void saveEntry(JournalEntry journalEntry) {
        try{
            journalEntryRepository.save(journalEntry);
        }
        catch(Exception e){
            log.error("Error saving journal entry", e);
        }

    }

    public List<JournalEntry> getAllEntries() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteEntryById(ObjectId id, String userName) {
        boolean removed =  false;
        try{
            User user = userService.findUserByUsername(userName);
            removed = user.getJournalEntries().removeIf(journalEntry -> journalEntry.getId().equals(id));
            if (removed){
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        }
        catch(Exception e){
            log.error("Error deleting journal entry", e);
            throw new RuntimeException("Error deleting journal entry");
        }
        return removed;

    }


}
