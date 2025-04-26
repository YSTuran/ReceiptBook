package yusufs.turan.receiptbook.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import yusufs.turan.receiptbook.model.Receipt

@Database(entities = [Receipt::class], version = 1)
abstract class ReceiptDB : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDAO
}