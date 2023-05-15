package me.drpot.staffnotes.manager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private MongoCollection<Document> notesCollection;

    public DatabaseManager(String databaseUri) {
        MongoClientURI uri = new MongoClientURI(databaseUri);
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase(uri.getDatabase());
        notesCollection = database.getCollection("notes");
    }

    public void savePlayerNote(String player, String noteTitle, String noteContent, String savedBy) {
        //todo add date to notes
        Document noteDocument = new Document("player", player)
                .append("title", noteTitle)
                .append("content", noteContent)
                .append("saved_by", savedBy);

        notesCollection.insertOne(noteDocument);
    }

    public void removePlayerNote(String player, String noteTitle) {
        Document query = new Document("player", player).append("title", noteTitle);
        notesCollection.deleteOne(query);
    }

    public void updatePlayerNote(String player, String noteTitle, String newNoteContent) {
        Document query = new Document("player", player).append("title", noteTitle);
        Document update = new Document("$set", new Document("content", newNoteContent));
        notesCollection.updateOne(query, update);
    }

    public List<Document> getPlayerNotes(String player) {
        Document query = new Document("player", player);
        List<Document> playerNotes = new ArrayList<>();

        for (Document note : notesCollection.find(query)) {
            playerNotes.add(note);
        }

        return playerNotes;
    }
}