package gov.anzong.androidnga.activity.compose

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.android.arouter.launcher.ARouter
import gov.anzong.androidnga.arouter.ARouterConstants
import sp.phone.common.UserManagerImpl

class SearchViewModel : ViewModel() {

    companion object {
        const val SEARCH_MODE_USER: Int = 0
        const val SEARCH_MODE_TOPIC: Int = 1
        const val SEARCH_MODE_BOARD: Int = 2

        const val SEARCH_MODE_USER_NAME: String = "username"
        const val SEARCH_MODE_USER_ID: String = "uid"
    }

    val searchData: List<Pair<String, Int>> = arrayListOf(
        Pair("搜主题", SEARCH_MODE_TOPIC),
        Pair("搜板块", SEARCH_MODE_BOARD),
        Pair("搜用户", SEARCH_MODE_USER),
    )

    val searchUserData: List<Pair<String, String>> = arrayListOf(
        Pair("用户名", SEARCH_MODE_USER_NAME),
        Pair("用户ID", SEARCH_MODE_USER_ID),
    )

    var searchMode: MutableLiveData<Int> = MutableLiveData(SEARCH_MODE_TOPIC)

    var searchUserMode: String = SEARCH_MODE_USER_NAME


    fun getSearchTintText(searchMode: Int): String {
        return when (searchMode) {
            SEARCH_MODE_USER -> "默认查看自己的用户信息"
            SEARCH_MODE_BOARD -> "强撸灰飞烟灭"
            SEARCH_MODE_TOPIC -> "强撸灰飞烟灭"
            else -> "搜索"
        }
    }

    fun query(query: String) {
        var realQuery = query
        if (query.isEmpty()) {
            val user = UserManagerImpl.getInstance().activeUser ?: return
            realQuery = if (searchUserMode == SEARCH_MODE_USER_NAME) user.nickName else user.userId
        }
        ARouter.getInstance()
            .build(ARouterConstants.ACTIVITY_PROFILE)
            .withString("mode", searchUserMode)
            .withString(searchUserMode, realQuery)
            .navigation()
    }

}
