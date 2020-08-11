package com.example.fragments

import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.fragments.movie.MovieJson
import com.example.fragments.movie.MovieResults
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import layout.MoviesAdapter
import layout.onMovieItemClickListener
import java.lang.reflect.Type
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class SearchFragment() : Fragment(), onMovieItemClickListener {

    lateinit var moviesList: ArrayList<Movie>
    lateinit var moviesAdapter: MoviesAdapter

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        parseJsonToView()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        initRecyclerView(rootView)
        initSearchBar(rootView)
        return rootView
    }

    private fun parseJsonToView() {
        val json = this.context?.let { MovieInflater.getJsonDataFromAsset(it, "movies.json") }.toString()
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<MovieResults> = moshi.adapter(MovieResults::class.java)
        val moviesData = jsonAdapter.fromJson(json)
        val moviesHehe: List<MovieJson> = moviesData!!.results
        moviesList = MovieInflater.createTrialList(moviesHehe)
        moviesAdapter = MoviesAdapter(moviesList, this)
    }

    private fun initRecyclerView(view: View) {
        val rvMovies = view.findViewById<View>(R.id.moviesRecyclerView) as? RecyclerView
        rvMovies?.layoutManager = LinearLayoutManager(this.context)
        rvMovies?.adapter = moviesAdapter
    }

    private fun initSearchBar(view: View) {
        val searchField = view.findViewById<View>(R.id.searchField) as? EditText
        var timer = Timer()
        timerSchedule(timer, searchField!!.text)

        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                if (searchField.text.isNotBlank()) {
                    timerSchedule(timer, searchField.text)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )

    }

    private fun timerSchedule(timer: Timer, textToWrite: Editable) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {

                    Toast.makeText(activity?.applicationContext, textToWrite, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }, 500L)
    }

    override fun onItemClick(item: Movie, position: Int) {
        val bundle = Bundle()
        val toSend = arrayListOf<String>(
            item.title,
            item.id.toString(),
            item.releaseDate.toString(),
            item.voteAverage.toString()
        )
        bundle.putStringArrayList("datas", toSend)
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
