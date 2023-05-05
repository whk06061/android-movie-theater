package woowacourse.movie.view.widget

import android.os.Bundle
import android.widget.Spinner

class SaveStateSpinner(override val saveStateKey: String, val spinner: Spinner) : SaveState {

    override fun save(outState: Bundle) {
        outState.putInt(saveStateKey, spinner.selectedItemPosition)
    }

    override fun load(savedInstanceState: Bundle?): Int {
        return savedInstanceState?.getInt(saveStateKey) ?: 0
    }
}
