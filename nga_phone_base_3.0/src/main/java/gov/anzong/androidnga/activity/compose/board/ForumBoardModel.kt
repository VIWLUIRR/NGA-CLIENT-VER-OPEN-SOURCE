package gov.anzong.androidnga.activity.compose.board

import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.base.util.ContextUtils
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.util.ThreadUtils
import gov.anzong.androidnga.common.PreferenceKey
import gov.anzong.androidnga.core.board.data.Board
import gov.anzong.androidnga.core.board.data.BoardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections

class ForumBoardModel {

    private lateinit var boardList: MutableList<BoardEntity>

    lateinit var bookmarkBoard: BoardEntity

    private val boardMap: HashMap<String, BoardEntity> = HashMap()

    private val saveTask = kotlinx.coroutines.Runnable {
        CoroutineScope(Dispatchers.IO).launch {
            ForumBoardRepository.writeBoardList(
                ContextUtils.getContext(),
                boardList.toMutableList()
            )
        }
    }

    init {
        boardList = ForumBoardRepository.loadLocalBoardList(ContextUtils.getContext())
        boardList.forEach {
            initBoardData(it, null)
        }
        transferBookmarkBoards()
    }

    private fun initBoardData(boardEntity: BoardEntity, parent: BoardEntity? = null) {
        with(boardEntity) {
            parentId = parent?.id
            id = generateBoardId(fid, stid, parentId) ?: id
        }
        if (boardEntity.type == BoardEntity.BoardType.BOOKMARK) {
            bookmarkBoard = boardEntity
        }
        boardMap[boardEntity.id] = boardEntity
        boardEntity.children?.let {
            it.forEach { data ->
                initBoardData(data)
            }
        }
    }

    fun loadBoardData(): MutableList<BoardEntity> {
        return boardList
    }

    fun addBookmarkBoard(name: String, fid: Int, stid: Int): Int {
        val id = generateBoardId(fid, stid)
        val boardEntity = boardMap[id] ?: BoardEntity().also {
            it.fid = fid
            it.stid = stid
            it.id = id!!
            it.name = name
        }
        if (!bookmarkBoard.children!!.contains(boardEntity)) {
            bookmarkBoard.children!!.add(boardEntity)
            boardMap[id!!] = boardEntity
            saveData()
        }
        return bookmarkBoard.children!!.size
    }

    fun removeBookmarkBoard(fid: Int, stid: Int): Int {
        val id = generateBoardId(fid, stid)
        val boardEntity = boardMap[id]
        if (boardEntity != null) {
            boardMap.remove(id)
            bookmarkBoard.children!!.remove(boardEntity)
            saveData()
        }
        return bookmarkBoard.children!!.size
    }

    fun removeAllBookmarkBoard(): Int? {
        return bookmarkBoard.children?.let {
            it.clear()
            saveData()
            return 0
        }
    }

    private fun transferBookmarkBoards() {
        if (!bookmarkBoard.children.isNullOrEmpty()) {
            return
        }
        val bookmarkJson = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, "")
        if (bookmarkJson.isNullOrEmpty()) {
            return
        }
        val bookmarks: List<Board> = JSON.parseArray(bookmarkJson, Board::class.java)

        bookmarks.forEach {
            val boardEntity = BoardEntity()
            boardEntity.fid = it.fid
            boardEntity.name = it.name.toString()
            if (it.stid != 0) {
                boardEntity.stid = it.stid
            }
            generateBoardId(boardEntity.fid, boardEntity.stid, bookmarkBoard.id)
            bookmarkBoard.children!!.add(boardEntity)
        }
        saveData(true)
        //PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, "")
    }

    private fun generateBoardId(fid: Int, stid: Int, parentId: String? = null): String? {
        var id: String? = null
        if (fid != 0) {
            id = fid.toString()
        }
        if (stid != 0) {
            id = id + "_" + stid
        } else if (parentId != null) {
            id = id + "_" + parentId
        }
        return id
    }

    fun swapBookmark(from: Int, to: Int) {
        val boards: List<BoardEntity> = bookmarkBoard.children!!
        if (from < to) {
            for (i in from until to) {
                Collections.swap(boards, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(boards, i, i - 1)
            }
        }
        saveData()
    }

    // 1s最多保存一次
    private fun saveData(rightNow: Boolean = false) {
        val delayTime = if (rightNow) 0 else 1000
        if (!ThreadUtils.hasRunnable(saveTask)) {
            ThreadUtils.postOnMainThreadDelay(saveTask, delayTime.toLong())
        }
    }

}