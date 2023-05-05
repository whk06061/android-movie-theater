package woowacourse.movie.fragment.reservationlist

import woowacourse.movie.domain.repository.ReservationRepository
import woowacourse.movie.view.data.ReservationViewData
import woowacourse.movie.view.data.ReservationsViewData
import woowacourse.movie.view.mapper.ReservationMapper.toView

class ReservationListPresenter(
    private val view: ReservationListContract.View,
    reservationDataSource: woowacourse.movie.domain.dataSource.ReservationDataSource
) :
    ReservationListContract.Presenter {

    private val reservationRepository = ReservationRepository(reservationDataSource)

    override fun initReservationRecyclerView() {
        val reservationsViewData =
            reservationRepository.getData().map { reservation -> reservation.toView() }
                .let { ReservationsViewData(it) }
        view.initReservationRecyclerView(reservationsViewData)
    }

    override fun onItemClick(reservationViewData: ReservationViewData) {
        view.onItemClick(reservationViewData)
    }
}
