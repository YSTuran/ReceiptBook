package yusufs.turan.receiptbook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Receipt(

    @ColumnInfo(name = "Name")
    var name: String,

    @ColumnInfo(name= "Ingredients")
    var ingredients: String,

    @ColumnInfo(name = "Image")
    var pic: ByteArray
)
{
    @PrimaryKey(autoGenerate = true)
    var id = 0
}