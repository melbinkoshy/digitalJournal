package net.melbin.journalApp.controller;

import net.melbin.journalApp.entity.JournalEntry;
import net.melbin.journalApp.entity.User;
import net.melbin.journalApp.service.JournalEntryService;
import net.melbin.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class journalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("/{userName}")
    public ResponseEntity<List<JournalEntry>> getAll(@PathVariable String userName) {
        User user = userService.findUserByUsername(userName);
        List<JournalEntry> entries = user.getJournalEntries();
        if (entries != null && !entries.isEmpty()) {
            return new ResponseEntity<List<JournalEntry>>(entries, HttpStatus.OK);
        }
        return new ResponseEntity<List<JournalEntry>>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry, @PathVariable String userName) {

        try{
            journalEntryService.saveEntry(entry,userName);
            return new ResponseEntity<JournalEntry>(entry, HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<JournalEntry>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("id/{id}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable ObjectId id) {
        Optional<JournalEntry> entry = journalEntryService.getEntryById(id);
        if(entry.isPresent()) {
            return new ResponseEntity<JournalEntry>(entry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<JournalEntry>( HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{userName}/{id}")
    public ResponseEntity<?> deleteEntryById(@PathVariable ObjectId id, @PathVariable String userName) {
        journalEntryService.deleteEntryById(id,userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{userName}/{id}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry,@PathVariable String userName) {
        JournalEntry oldEntry = journalEntryService.getEntryById(id).orElse(null);
        if (oldEntry != null) {
            oldEntry.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")?newEntry.getTitle():oldEntry.getTitle());
            oldEntry.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")?newEntry.getContent():oldEntry.getContent());
            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
}
