package gov.anzong.androidnga.activity.compose.filter

import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.base.kv.DataStore
import gov.anzong.androidnga.base.util.PreferenceUtils
import sp.phone.common.FilterKeyword
import sp.phone.common.User

object FilterManager {

    @Deprecated("错误的Key")
    private const val DEPRECATED_FILTER_USER_LIST = ""

    private const val FILTER_WORD_KEY = "filter_keywords"

    private const val FILTER_USER_KEY = "filter_user"

    private const val FILTER_FILE_NAME = "filter"

    val wordFilterList: MutableList<FilterKeyword> = mutableListOf()

    val userFilterList: MutableList<User> = mutableListOf()

    private val filterDataStore = DataStore.workAs(FILTER_FILE_NAME)

    init {
        initFilterUserList()
        initFilterWordList()
    }

    private fun initFilterUserList() {
        var dataStr = filterDataStore.getData(FILTER_USER_KEY, "")
        if (dataStr.isNullOrEmpty()) {
            dataStr = PreferenceUtils.getData(DEPRECATED_FILTER_USER_LIST, "")
            if (dataStr.isNotEmpty()) {
                filterDataStore.putData(FILTER_USER_KEY, dataStr)
            }
        }
        if (!dataStr.isNullOrEmpty()) {
            userFilterList.addAll(JSON.parseArray(dataStr, User::class.java))
            userFilterList.removeIf { user: User -> user.userId == null }
        }
    }

    private fun initFilterWordList() {
        var dataStr = filterDataStore.getData(FILTER_WORD_KEY, "")
        if (dataStr.isNullOrEmpty()) {
            dataStr = PreferenceUtils.getData(FILTER_WORD_KEY, "")
            if (dataStr.isNotEmpty()) {
                filterDataStore.putData(FILTER_WORD_KEY, dataStr)
            }
        }
        if (!dataStr.isNullOrEmpty()) {
            wordFilterList.addAll(JSON.parseArray(dataStr, FilterKeyword::class.java))
        }
    }

    fun filerByUser(uid: String): Boolean {
        for (user in userFilterList) {
            if (user.userId == uid) {
                return true
            }
        }
        return false
    }

    fun addFilterUser(userName: String, uid: String) {
        addFilterUser(User(uid, userName))
    }

    fun addFilterUser(user: User) {
        if (userFilterList.contains(user)) {
            return
        } else {
            userFilterList.add(user)
            filterDataStore.putData(FILTER_USER_KEY, JSON.toJSONString(userFilterList))
        }
    }

    fun removeFilterUser(user: User) {
        if (userFilterList.remove(user)) {
            filterDataStore.putData(FILTER_USER_KEY, JSON.toJSONString(userFilterList))
        }
    }

    fun removeFilterUser(uid: String) {
        removeFilterUser(User(uid, ""))
    }

    fun addFilterWord(word: String) {
        val filterWord = FilterKeyword(word)
        if (wordFilterList.contains(filterWord)) {
            return
        } else {
            wordFilterList.add(filterWord)
            filterDataStore.putData(FILTER_WORD_KEY, JSON.toJSONString(wordFilterList))
        }
    }

    fun removeFilterWord(word: String) {
        val filterWord = FilterKeyword(word)
        if (wordFilterList.remove(filterWord)) {
            filterDataStore.putData(FILTER_WORD_KEY, JSON.toJSONString(wordFilterList))
        }
    }

}