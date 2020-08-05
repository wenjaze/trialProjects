package com.example.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.movie.Movie
import com.example.fragments.movie.MovieInflater
import layout.MoviesAdapter
import layout.onMovieItemClickListener
import java.util.Timer
import java.util.TimerTask

class SearchFragment : Fragment(), onMovieItemClickListener {

    lateinit var moviesList: ArrayList<Movie>
    lateinit var moviesAdapter: MoviesAdapter

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        moviesList = MovieInflater.createTrialList()
        moviesAdapter = MoviesAdapter(moviesList, this)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        val rvMovies = rootView.findViewById<View>(R.id.moviesRecyclerView) as? RecyclerView
        val searchField = rootView.findViewById<View>(R.id.searchField) as? EditText
        searchField?.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                private var timer: Timer = Timer()
                private val DELAY: Long = 500
                override fun afterTextChanged(s: Editable) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            activity?.runOnUiThread {
                                Toast.makeText(activity?.applicationContext, searchField.text, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }, DELAY)
                }
            }
        )
        rvMovies?.layoutManager = LinearLayoutManager(this.context)
        rvMovies?.adapter = moviesAdapter
        return rootView
    }

    override fun onItemClick(item: Movie, position: Int) {
        val bundle = Bundle()
        bundle.putString("movieTitle", item.title)
        val secondFragment = SecondFragment()
        secondFragment.arguments = bundle
        switchToSecondFragment(secondFragment)
    }

    private fun switchToSecondFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()?.run {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
            commit()
        }

    }

}