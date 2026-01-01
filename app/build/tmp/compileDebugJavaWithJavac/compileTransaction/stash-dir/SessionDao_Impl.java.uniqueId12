package com.zenflow.mobile.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
public final class SessionDao_Impl implements SessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SessionEntity> __insertionAdapterOfSessionEntity;

  public SessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSessionEntity = new EntityInsertionAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sessions` (`id`,`startTs`,`endTs`,`type`,`completed`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final SessionEntity entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.startTs);
        if (entity.endTs == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.endTs);
        }
        if (entity.type == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.type);
        }
        final int _tmp = entity.completed ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
  }

  @Override
  public void insert(final SessionEntity s) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSessionEntity.insert(s);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<SessionEntity> getAll() {
    final String _sql = "SELECT * FROM sessions ORDER BY startTs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfStartTs = CursorUtil.getColumnIndexOrThrow(_cursor, "startTs");
      final int _cursorIndexOfEndTs = CursorUtil.getColumnIndexOrThrow(_cursor, "endTs");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
      final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final SessionEntity _item;
        _item = new SessionEntity();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.startTs = _cursor.getLong(_cursorIndexOfStartTs);
        if (_cursor.isNull(_cursorIndexOfEndTs)) {
          _item.endTs = null;
        } else {
          _item.endTs = _cursor.getLong(_cursorIndexOfEndTs);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfCompleted);
        _item.completed = _tmp != 0;
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
