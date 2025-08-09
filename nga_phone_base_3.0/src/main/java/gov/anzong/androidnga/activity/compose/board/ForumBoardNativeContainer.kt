package gov.anzong.androidnga.activity.compose.board

import android.annotation.SuppressLint
import android.content.Context
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.justwen.androidnga.ui.compose.theme.AppTheme

@SuppressLint("ViewConstructor")
class ForumBoardNativeContainer(context: Context, provider: ViewModelProvider) :
    FrameLayout(context) {

    private var forumBoardViewModel: ForumBoardViewModel = provider[ForumBoardViewModel::class.java]

    init {
        addView(ComposeView(context).apply {
            setContent {
                AppTheme {
                    ForumBoardView(forumBoardViewModel)
                }
            }
        })
    }

}

