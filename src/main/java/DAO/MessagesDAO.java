package DAO;

import Models.Chats;
import Models.Messages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessagesDAO implements InterfaceDAO<Messages> {
    private UUID currentUser;

    public void setCurrentUser(UUID currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void insert(Messages messages) {
        String sql = "INSERT INTO messages (messagesid, messagesuserfromid, messagesusertoid, messagetext, messagesdatetime) VALUES (?,?,?,?,?)";

        Connection connection = ConnectionToDB.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, messages.getMessagesId() );
            statement.setObject(2, messages.getMessagesuserfromid());
            statement.setObject(3, messages.getMessagesusertoid());
            statement.setString(4, messages.getMessagesText());
            statement.setObject(5, messages.getMessagesDateTime());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void update(Messages messages) {
        String sql = "UPDATE mESSAGES SET messagesid = ?, messagesuserfromid = ?, messagesusertoid = ?, messagesdatetime = ? WHERE messagesid = ?";
        Connection connection = ConnectionToDB.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, messages.getMessagesuserfromid());
            statement.setObject(2, messages.getMessagesusertoid());
            statement.setString(3, messages.getMessagesText());
            statement.setObject(4, messages.getMessagesDateTime());
            statement.setObject(5, messages.getMessagesId() );

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List get(String filter) {
        List<Chats> items = new ArrayList<>();

        String sql =
                "  SELECT " +
                "  CASE WHEN m.messagesuserfromid <> ? THEN u.usersfirstname ELSE '' END as speakToFirstName," +
                "  CASE WHEN m.messagesuserfromid <> ? THEN u.userslinkphoto ELSE '' END as speakToImage," +
                "  CASE WHEN m.messagesuserfromid = ? THEN m.messagetext ELSE '' END as currentuserText," +
                "  CASE WHEN m.messagesuserfromid <> ? THEN m.messagetext ELSE '' END as speakToText," +
                "  m.messagesdatetime as messagesDateTime" +
                "  FROM messages as m" +
                "  INNER JOIN users as u ON m.messagesusertoid = u.usersid";
        if (filter.length() > 0) {
            sql += " where " + filter;
        }

        try (
                Connection connection = ConnectionToDB.getConnection();

        ) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, this.currentUser);
            statement.setObject(2, this.currentUser);
            statement.setObject(3, this.currentUser);
            statement.setObject(4, this.currentUser);

            ResultSet rSet = statement.executeQuery();

            while (rSet.next()) {
                Chats chats = new Chats();
                chats.setMessagesDateTime(rSet.getDate("messagesdatetime"));
                chats.setCurrentText(rSet.getString("currentusertext"));
                chats.setSpeakToFirstName(rSet.getString("speaktofirstname"));
                chats.setSpeakToImage(rSet.getString("speaktoimage"));
                chats.setSpeakToText(rSet.getString("speaktotext"));
                items.add(chats);
                //Messages messages = new Messages();
                /*messages.setMessagesuserfromid(UUID.fromString(rSet.getString("messagesuserfromid")));
                messages.setMessagesId(UUID.fromString(rSet.getString("messagesid")));
                messages.setMessagesusertoid(UUID.fromString(rSet.getString("messagesusertoid")));
                messages.setMessagestext(rSet.getString("messagestext"));
                messages.setMessagesDateTime(rSet.getDate("messagesdatetime"));

                *///items.add(messages);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public void delete(Messages messages) {
        throw new IllegalArgumentException("not implemented");
    }


}