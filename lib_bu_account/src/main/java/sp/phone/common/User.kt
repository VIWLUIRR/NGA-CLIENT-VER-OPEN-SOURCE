package sp.phone.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import gov.anzong.androidnga.common.base.JavaBean

/**
 * @author Justwen
 * @date 2017/12/26
 */
@Entity(tableName = "users")
open class User : JavaBean {

    @ColumnInfo(name = "cid")
    var cid: String = ""

    @PrimaryKey
    @ColumnInfo(name = "uid")
    var userId: String = ""

    @ColumnInfo(name = "nick_name")
    var nickName: String = ""

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String? = null

    constructor()

    @Ignore
    constructor(userId: String, nickName: String, cid: String = "") {
        this.userId = userId
        this.nickName = nickName
        this.cid = cid
    }

    override fun equals(other: Any?): Boolean {
        return other is User && userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

    override fun toString(): String {
        return "User{" +
                "mCid='" + cid + '\'' +
                ", mUserId='" + userId + '\'' +
                ", mNickName='" + nickName + '\'' +
                ", mAvatarUrl='" + avatarUrl + '\'' +
                '}'
    }
}
