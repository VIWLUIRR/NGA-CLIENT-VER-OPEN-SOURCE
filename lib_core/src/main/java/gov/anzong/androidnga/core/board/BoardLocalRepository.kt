package gov.anzong.androidnga.core.board

import android.content.Context
import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.base.utils.Strings
import gov.anzong.androidnga.core.board.data.BoardEntity
import java.io.File

object BoardLocalRepository {

    private const val BOARD_FILE_NAME = "board_list.json"

    fun checkBoardData(board: BoardEntity, parent: BoardEntity?) {

        if (board.parentId.isNullOrEmpty()) {
            board.parentId = parent?.id
        }

        if (board.id.isEmpty()) {
            var id = ""
            if (board.type == BoardEntity.BoardType.GROUP) {
                if (parent?.id != null) {
                    id = parent.id
                }
                id = id + "_" + board.name
            } else {
                if (!board.fid.isNullOrEmpty()) {
                    id = board.fid!!
                }

                if (!board.stid.isNullOrEmpty()) {
                    id = id + "_" + board.stid
                }
            }
            board.id = id
        }
    }

    fun getBoardList(context: Context): MutableList<BoardEntity> {
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

}