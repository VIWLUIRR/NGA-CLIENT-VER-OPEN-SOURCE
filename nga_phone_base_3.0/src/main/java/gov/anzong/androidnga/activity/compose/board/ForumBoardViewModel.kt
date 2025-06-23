package gov.anzong.androidnga.activity.compose.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gov.anzong.androidnga.arouter.ARouterConstants
import gov.anzong.androidnga.core.board.data.BoardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import sp.phone.param.ParamKey
import sp.phone.util.ARouterUtils

object ForumBoardViewModel : ViewModel() {

    val boardLiveData: MutableLiveData<List<BoardEntity>> = MutableLiveData()

    val bookmarkSizeLiveData: MutableLiveData<Int> = MutableLiveData(0)

    private val forumBoardModel = ForumBoardModel()

    init {
        boardLiveData.postValue(forumBoardModel.loadBoardData())
        bookmarkSizeLiveData.postValue(forumBoardModel.bookmarkBoard.children?.size)
    }

    fun getBoardData(index: Int = 0): BoardEntity {
        return boardLiveData.value!![index]
    }

    fun addBookmarkBoard(name: String, fid: Int, stid: Int) {
        bookmarkSizeLiveData.value = forumBoardModel.addBookmarkBoard(name, fid, stid)
    }

    fun removeBookmarkBoard(fid: Int, stid: Int) {
        bookmarkSizeLiveData.value = forumBoardModel.removeBookmarkBoard(fid, stid)
    }

    fun removeAllBookmarkBoard() {
        forumBoardModel.removeAllBookmarkBoard()?.let {
            bookmarkSizeLiveData.value = it
        }
    }

    fun showTopicList(board: BoardEntity) {
        val fid = board.fid
        val stid = board.stid
        ARouterUtils.build(ARouterConstants.ACTIVITY_TOPIC_LIST)
            .withInt(ParamKey.KEY_FID, fid)
            .withInt(ParamKey.KEY_STID, stid)
            .withString(ParamKey.KEY_TITLE, board.name)
            .navigation()
    }

    fun mergeRemoteBoardList() {
        viewModelScope.launch(Dispatchers.IO) {
            val job = async {
                return@async forumBoardModel.loadIncrementalBoardList()
            }
            val result = job.await()
            if (result.isNotEmpty()) {
                forumBoardModel.mergeBoardList(result)
            }
        }
    }
}