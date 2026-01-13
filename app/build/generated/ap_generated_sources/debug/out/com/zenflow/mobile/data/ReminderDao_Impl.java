package com.zenflow.mobile.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReminderDao_Impl implements ReminderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReminderEntity> __insertionAdapterOfReminderEntity;

  private final EntityDeletionOrUpdateAdapter<ReminderEntity> __deletionAdapterOfReminderEntity;

  public ReminderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReminderEntity = new EntityInsertionAdapter<ReminderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reminders` (`id`,`dateEpochDay`,`createdTs`,`text`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ReminderEntity entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.dateEpochDay);
        statement.bindLong(3, entity.createdTs);
        if (entity.text == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.text);
        }
      }
    };
    this.__deletionAdapterOfReminderEntity = new EntityDeletionOrUpdateAdapter<ReminderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reminders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ReminderEntity entity) {
        statement.bindLong(1, entity.id);
      }
    };
  }

  @Override
  public void insert(final ReminderEntity r) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfReminderEntity.insert(r);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final ReminderEntity r) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfReminderEntity.handle(r);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<ReminderEntity> getByEpochDay(final long epochDay) {
    final String _sql = "SELECT * FROM reminders WHERE dateEpochDay = ? ORDER BY createdTs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, epochDay);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDateEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEpochDay");
      final int _cursorIndexOfCreatedTs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdTs");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final List<ReminderEntity> _result = new ArrayList<ReminderEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ReminderEntity _item;
        _item = new ReminderEntity();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.dateEpochDay = _cursor.getLong(_cursorIndexOfDateEpochDay);
        _item.createdTs = _cursor.getLong(_cursorIndexOfCreatedTs);
        if (_cursor.isNull(_cursorIndexOfText)) {
          _item.text = null;
        } else {
          _item.text = _cursor.getString(_cursorIndexOfText);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ReminderEntity> getUpcoming(final long startEpochDay, final long endEpochDay) {
    final String _sql = "SELECT * FROM reminders WHERE dateEpochDay BETWEEN ? AND ? ORDER BY dateEpochDay ASC, createdTs ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startEpochDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endEpochDay);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDateEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEpochDay");
      final int _cursorIndexOfCreatedTs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdTs");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final List<ReminderEntity> _result = new ArrayList<ReminderEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ReminderEntity _item;
        _item = new ReminderEntity();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.dateEpochDay = _cursor.getLong(_cursorIndexOfDateEpochDay);
        _item.createdTs = _cursor.getLong(_cursorIndexOfCreatedTs);
        if (_cursor.isNull(_cursorIndexOfText)) {
          _item.text = null;
        } else {
          _item.text = _cursor.getString(_cursorIndexOfText);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
