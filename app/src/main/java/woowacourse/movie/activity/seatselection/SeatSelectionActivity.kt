package woowacourse.movie.activity.seatselection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import woowacourse.movie.R
import woowacourse.movie.activity.reservationresult.ReservationResultActivity
import woowacourse.movie.service.AlarmMaker
import woowacourse.movie.view.data.MovieViewData
import woowacourse.movie.view.data.PriceViewData
import woowacourse.movie.view.data.ReservationDetailViewData
import woowacourse.movie.view.data.ReservationViewData
import woowacourse.movie.view.data.SeatsViewData
import woowacourse.movie.view.error.ActivityError.finishWithError
import woowacourse.movie.view.error.ViewError
import woowacourse.movie.view.getSerializable
import woowacourse.movie.view.widget.SaveStateSeats
import woowacourse.movie.view.widget.SeatTableLayout
import java.time.LocalDateTime

class SeatSelectionActivity : AppCompatActivity(), SeatSelectionContract.View {
    override lateinit var presenter: SeatSelectionContract.Presenter

    private val priceText: TextView by lazy {
        findViewById(R.id.seat_selection_movie_price)
    }

    private val reservationButton: Button by lazy {
        findViewById(R.id.seat_selection_reserve_button)
    }

    private val seatTableLayout: SeatTableLayout by lazy {
        SeatTableLayout.from(
            findViewById(R.id.seat_selection_table),
            SEAT_ROW_COUNT,
            SEAT_COLUMN_COUNT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_selection)

        presenter = SeatSelectionPresenter(
            this,
            SaveStateSeats(SEAT_TABLE_LAYOUT_STATE_KEY, seatTableLayout)
        )
        initSeatSelectionView(savedInstanceState)
    }

    private fun initSeatSelectionView(savedInstanceState: Bundle?) {
        val movie = intent.extras?.getSerializable<MovieViewData>(MovieViewData.MOVIE_EXTRA_NAME)
            ?: return finishWithError(ViewError.MissingExtras(MovieViewData.MOVIE_EXTRA_NAME))
        val reservationDetail =
            intent.extras?.getSerializable<ReservationDetailViewData>(ReservationDetailViewData.RESERVATION_DETAIL_EXTRA_NAME)
                ?: return finishWithError(ViewError.MissingExtras(ReservationDetailViewData.RESERVATION_DETAIL_EXTRA_NAME))

        initMovieView(movie)
        presenter.initPrice(PriceViewData())
        initSeatTableLayout(movie, reservationDetail, savedInstanceState)
    }

    private fun initSeatTableLayout(
        movie: MovieViewData,
        reservationDetail: ReservationDetailViewData,
        savedInstanceState: Bundle?
    ) {
        initReserveButton(seatTableLayout, movie, reservationDetail)

        makeBackButton()

        seatTableLayout.onSelectSeat = {
            onSelectSeat(it, reservationDetail)
        }

        seatTableLayout.seatSelectCondition = { seatsSize ->
            seatsSize < reservationDetail.peopleCount
        }
        presenter.loadSeat(savedInstanceState)
    }

    private fun makeBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun onSelectSeat(seats: SeatsViewData, reservationDetail: ReservationDetailViewData) {
        presenter.setSeatsPrice(seats, reservationDetail)
        setReservationButtonState(seats.seats.size, reservationDetail.peopleCount)
    }

    override fun setPriceView(price: String) {
        priceText.text = price
    }

    override fun makeAlarm(alarmDate: LocalDateTime, reservation: ReservationViewData) {
        AlarmMaker.make(alarmDate, reservation, this)
    }

    private fun setReservationButtonState(
        seatsSize: Int,
        peopleCount: Int
    ) {
        reservationButton.isEnabled = seatsSize == peopleCount
    }

    private fun initReserveButton(
        seatTableLayout: SeatTableLayout,
        movie: MovieViewData,
        reservationDetail: ReservationDetailViewData
    ) {
        reservationButton.setOnClickListener {
            onClickReserveButton(seatTableLayout, movie, reservationDetail)
        }
        setReservationButtonState(DEFAULT_SEAT_SIZE, reservationDetail.peopleCount)
    }

    private fun onClickReserveButton(
        seatTableLayout: SeatTableLayout,
        movie: MovieViewData,
        reservationDetail: ReservationDetailViewData
    ) {
        AlertDialog.Builder(this).setTitle(getString(R.string.seat_selection_alert_title))
            .setMessage(getString(R.string.seat_selection_alert_message))
            .setPositiveButton(getString(R.string.seat_selection_alert_positive)) { _, _ ->
                reserveMovie(seatTableLayout, movie, reservationDetail)
            }.setNegativeButton(getString(R.string.seat_selection_alert_negative)) { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false).show()
    }

    private fun reserveMovie(
        seatTableLayout: SeatTableLayout,
        movie: MovieViewData,
        reservationDetail: ReservationDetailViewData
    ) {
        val seats = seatTableLayout.selectedSeats()
        presenter.reserveMovie(seats, movie, reservationDetail)
    }

    override fun startReservationResultActivity(
        reservation: ReservationViewData
    ) {
        ReservationResultActivity.from(
            this, reservation
        ).run {
            startActivity(this)
        }
    }

    override fun setSeatsClick(row: Int, column: Int) {
        seatTableLayout.findSeatViewByRowAndColumn(row, column)?.callOnClick()
    }

    private fun initMovieView(movie: MovieViewData) {
        findViewById<TextView>(R.id.seat_selection_movie_title).text = movie.title
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.saveSeat(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ACTION_ALARM = "actionAlarm"

        private const val SEAT_ROW_COUNT = 5
        private const val SEAT_COLUMN_COUNT = 4
        private const val DEFAULT_SEAT_SIZE = 0
        private const val SEAT_TABLE_LAYOUT_STATE_KEY = "seatTable"

        fun from(
            context: Context,
            movie: MovieViewData,
            reservationDetailViewData: ReservationDetailViewData
        ): Intent {
            return Intent(context, SeatSelectionActivity::class.java).apply {
                putExtra(MovieViewData.MOVIE_EXTRA_NAME, movie)
                putExtra(
                    ReservationDetailViewData.RESERVATION_DETAIL_EXTRA_NAME,
                    reservationDetailViewData
                )
            }
        }
    }
}
