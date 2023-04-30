package woowacourse.movie.view.error

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

object FragmentError {

    private const val LOG_TAG = "DYDY"
    private const val ERROR_LOG_MESSAGE = "%s 프래그먼트에서 %s Extra가 필요합니다."
    private const val ERROR_TOAST_MESSAGE = "프래그먼트 실행에 오류가 발생했습니다."
    private const val NULL_VIEW_LOG_MESSAGE = "뷰가 null 입니다."

    fun Fragment.finishWithError(missingExtras: ViewError.MissingExtras): View? {
        Log.d(
            LOG_TAG,
            ERROR_LOG_MESSAGE.format(this, missingExtras.message)
        )
        Toast.makeText(activity, ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT)
            .show()
        return null
    }

    fun Fragment.finishWithNullViewError() {
        Log.d(
            LOG_TAG,
            ERROR_LOG_MESSAGE.format(this, NULL_VIEW_LOG_MESSAGE)
        )
        Toast.makeText(activity, ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT)
            .show()
        return
    }
}
