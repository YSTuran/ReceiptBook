package yusufs.turan.receiptbook.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import yusufs.turan.receiptbook.model.Receipt


@Dao
interface ReceiptDAO {

    @Query("Select * from Receipt")
    fun getAll() : List<Receipt>

    @Query("Select * from Receipt where id = :id")
    fun findById(id : Int) : Receipt

    @Insert
    fun insert(receipt: Receipt)

    @Delete
    fun delete(receipt: Receipt)

}