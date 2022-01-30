package flag.com.ncubus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private Context context;

    public MySQLiteHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Blossom.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS EasyCard(_Code Text primary key)");
        db.execSQL("CREATE TABLE IF NOT EXISTS FarvoriteBus(_BusID TEXT Not Null, _Direction INTEGER Not Null, PRIMARY KEY (_BusID, _Direction))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        // TODO Auto-generated method stub
        db.execSQL("drop table if exists diary");
        onCreate(db);
    }
}

