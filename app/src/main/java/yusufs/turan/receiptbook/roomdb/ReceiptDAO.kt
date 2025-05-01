package yusufs.turan.receiptbook.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import yusufs.turan.receiptbook.model.Receipt


@Dao
interface ReceiptDAO {

    @Query("Select * from Receipt")
    fun getAll() : Flowable<List<Receipt>>

    @Query("Select * from Receipt where id = :id")
    fun findById(id : Int) : Flowable<Receipt>

    @Insert
    fun insert(receipt: Receipt) : Completable

    @Delete
    fun deleteData(receipt: Receipt) : Completable

}