package gov.anzong.androidnga.activity.compose.board

import android.content.Context
import com.alibaba.fastjson.JSON
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper
import gov.anzong.androidnga.Utils
import gov.anzong.androidnga.activity.compose.board.data.ForumsListBean
import gov.anzong.androidnga.base.utils.Strings
import gov.anzong.androidnga.core.board.data.BoardEntity
import java.io.File

object ForumBoardRepository {

    private const val BOARD_FILE_NAME = "board_list.json"

    private const val FORUM_URL: String = "app_api.php?__lib=home&__act=category"

    fun loadLocalBoardList(context: Context): MutableList<BoardEntity> {
        val boardJson: String
        val fileName = BOARD_FILE_NAME
        val dataFile = File(context.filesDir, fileName)

        boardJson = if (!dataFile.exists()) {
            Strings.readAssetString(context, fileName)
        } else {
            Strings.readFile(dataFile)
        }

        val boardList: MutableList<BoardEntity> = JSON.parseArray(
            boardJson,
            BoardEntity::class.java
        )
        return boardList
    }

    fun writeBoardList(context: Context, boardList: List<BoardEntity>) {
        val boardJson = JSON.toJSONString(boardList)
        val fileName = BOARD_FILE_NAME
        val dataFile = File(context.filesDir, fileName)
        Strings.writeFile(dataFile, boardJson)
    }

    suspend fun loadRemoteBoardList(context: Context): ForumsListBean {
        val url = Utils.getNGAHost() + FORUM_URL
        val result = RetrofitHelper.getInstance().serviceKt.getString(url)
        return JSON.parseObject(result, ForumsListBean::class.java)
    }

}