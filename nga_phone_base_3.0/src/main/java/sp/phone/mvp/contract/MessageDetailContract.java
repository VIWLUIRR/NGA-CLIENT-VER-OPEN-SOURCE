package sp.phone.mvp.contract;

import gov.anzong.androidnga.http.OnHttpCallBack;
import com.justwen.androidnga.core.data.MessageDetailInfo;

/**
 * Created by Justwen on 2017/10/11.
 */

public interface MessageDetailContract {

    interface IMessageView {

        void hideLoadingView();

        void setData(MessageDetailInfo listInfo);

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

    }

    interface IMessagePresenter {

        void loadPage(int page, int mid);
    }

    interface IMessageModel {

        void loadPage(int page, int mid, OnHttpCallBack<MessageDetailInfo> callBack);
    }

}
