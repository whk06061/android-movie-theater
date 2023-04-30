package woowacourse.movie

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import woowacourse.movie.activity.ReservationResultActivity
import woowacourse.movie.activity.SeatSelectionActivity
import woowacourse.movie.fragment.SettingFragment
import woowacourse.movie.view.data.ReservationViewData
import woowacourse.movie.view.error.BroadcastReceiverError.returnWithError
import woowacourse.movie.view.error.ViewError
import woowacourse.movie.view.getSerializable

class ReservationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val setting: Setting = SharedSetting(context)
        if (intent.action == SeatSelectionActivity.ACTION_ALARM && setting.getValue(
                SettingFragment.SETTING_NOTIFICATION
            )
        ) {
            val reservation =
                intent.extras?.getSerializable<ReservationViewData>(ReservationViewData.RESERVATION_EXTRA_NAME)
                    ?: return returnWithError(ViewError.MissingExtras(ReservationViewData.RESERVATION_EXTRA_NAME))

            val pendingIntent = makeNotificationPendingIntent(context, reservation)
            val builder = makeNotificationBuilder(context, reservation, pendingIntent)
            notifyNotification(context, builder)
        }
    }

    private fun makeNotificationPendingIntent(
        context: Context,
        reservation: ReservationViewData
    ): PendingIntent {
        val reservationIntent = ReservationResultActivity.from(context, reservation).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            SeatSelectionActivity.RESERVATION_REQUEST_CODE,
            reservationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun makeNotificationBuilder(
        context: Context,
        reservation: ReservationViewData,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, RESERVATION_NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(context.getString(R.string.notification_content_title))
            setContentText(
                context.getString(
                    R.string.notification_content_text, reservation.movie.title
                )
            )
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }
    }

    private fun notifyNotification(context: Context, builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    companion object {
        const val RESERVATION_NOTIFICATION_CHANNEL_ID = "reservation"
        const val NOTIFICATION_ID = 5

        fun from(context: Context, reservation: ReservationViewData): PendingIntent {
            val intent: Intent =
                Intent(SeatSelectionActivity.ACTION_ALARM).putExtra(
                    ReservationViewData.RESERVATION_EXTRA_NAME,
                    reservation
                )
            return PendingIntent.getBroadcast(
                context,
                SeatSelectionActivity.RESERVATION_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
