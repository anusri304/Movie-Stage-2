package com.example.movie.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.movie.dao.MovieDao;
import com.example.movie.domain.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieRoomDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();
    private static final Object LOCK = new Object();

    private static volatile MovieRoomDatabase movieRoomDatabase;
    private static final String DATABASE_NAME = "word_database";

    public static MovieRoomDatabase getDatabase(final Context context) {
        if (movieRoomDatabase == null) {
            synchronized (LOCK) {
                if (movieRoomDatabase == null) {
                    movieRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            MovieRoomDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return movieRoomDatabase;
    }

}
