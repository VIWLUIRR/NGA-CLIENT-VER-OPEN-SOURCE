package gov.anzong.androidnga.core.board

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

object BoardManager {

    private lateinit var boardList: MutableList<BoardEntity>

    private lateinit var bookmark: BoardEntity

    private val boardMap: HashMap<String, BoardEntity> = HashMap()

    private val saveTask = kotlinx.coroutines.Runnable {
        CoroutineScope(Dispatchers.IO).launch {
            BoardLocalRepository.writeBoardList(ContextUtils.getContext(), boardList)
        }
    }

    init {
        boardList = BoardLocalRepository.getBoardList(ContextUtils.getContext())
        boardList.forEach {
            initBoardData(it)
        }
        transferBookmarkBoards()
    }

    private fun initBoardData(boardEntity: BoardEntity) {
        if (boardEntity.type == BoardEntity.BoardType.BOOKMARK) {
            bookmark = boardEntity
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

    fun addBookmarkBoard(boardEntity: BoardEntity): Boolean {
        return if (!bookmark.children!!.contains(boardEntity)) {
            bookmark.children!!.add(boardEntity)
        } else {
            false
        }
    }

    fun removeBookmarkBoard(boardEntity: BoardEntity): Boolean {
        return bookmark.children!!.remove(boardEntity)
    }

    private fun transferBookmarkBoards() {
        if (bookmark.children?.isEmpty() == true) {
            return
        }
        val bookmarkJson = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, "")
        if (bookmarkJson.isNullOrEmpty()) {
            return
        }
        val bookmarks: List<Board> = JSON.parseArray(bookmarkJson, Board::class.java)

        bookmarks.forEach {
            val boardEntity = BoardEntity()
            boardEntity.fid = it.fid.toString()
            boardEntity.name = it.name.toString()
            if (it.stid != 0) {
                boardEntity.stid = it.stid.toString()
            }
            BoardLocalRepository.checkBoardData(boardEntity, bookmark)
            bookmark.children!!.add(boardEntity)
        }
        saveData(true)
        PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, "")
    }

    fun swapBookmark(from: Int, to: Int) {
        val boards: List<BoardEntity> = bookmark.children!!
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