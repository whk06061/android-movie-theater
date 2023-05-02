package woowacourse.movie.domain.seat

import woowacourse.movie.domain.discountPolicy.Discount
import woowacourse.movie.domain.discountPolicy.MovieDay
import woowacourse.movie.domain.discountPolicy.OffTime
import woowacourse.movie.domain.model.Price
import woowacourse.movie.domain.model.ReservationDetail

data class Seats(val value: List<Seat>) {
    fun calculateDiscountedPrice(
        reservationDetail: ReservationDetail
    ): Price {
        val discount = Discount(listOf(MovieDay, OffTime))
        return value.sumOf { seat ->
            discount.calculate(
                reservationDetail, seat.row.seatRankByRow().price
            ).value
        }.let {
            Price(it)
        }
    }
}
