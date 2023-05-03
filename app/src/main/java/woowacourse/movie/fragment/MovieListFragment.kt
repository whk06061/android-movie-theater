package woowacourse.movie.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import woowacourse.movie.R
import woowacourse.movie.activity.MovieReservationActivity
import woowacourse.movie.datasource.MockAdDataSource
import woowacourse.movie.datasource.MockMovieDataSource
import woowacourse.movie.domain.advertismentPolicy.MovieAdvertisementPolicy
import woowacourse.movie.domain.repository.AdRepository
import woowacourse.movie.domain.repository.MovieRepository
import woowacourse.movie.view.adapter.MovieAdapter
import woowacourse.movie.view.data.MovieListViewData
import woowacourse.movie.view.data.MovieListViewType
import woowacourse.movie.view.data.MovieViewData

class MovieListFragment : Fragment() {
    private val movieRepository: MovieRepository = MovieRepository(MockMovieDataSource())
    private val adRepository: AdRepository = AdRepository(MockAdDataSource())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeMovieRecyclerView(view)
    }

    private fun makeMovieRecyclerView(view: View) {
        val movies = movieRepository.getData()
        val advertisementDatas = adRepository.getData()
        val advertisementPolicy = MovieAdvertisementPolicy(MOVIE_COUNT, ADVERTISEMENT_COUNT)

        val movieRecyclerView = view.findViewById<RecyclerView>(R.id.main_movie_list)
        movieRecyclerView.adapter = MovieAdapter(
            movies, advertisementDatas, advertisementPolicy, ::onClickItem
        )
    }

    private fun onClickItem(view: View, data: MovieListViewData) {
        when (data.viewType) {
            MovieListViewType.MOVIE -> MovieReservationActivity.from(
                view.context, data as MovieViewData
            ).run {
                startActivity(this)
            }
            MovieListViewType.ADVERTISEMENT -> Unit
        }
    }

    companion object {
        private const val MOVIE_COUNT = 3
        private const val ADVERTISEMENT_COUNT = 1
    }
}
