package com.graytsar.idleclickercompanion

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var list:List<AppModel>? = null
    private var adapter:AppCardAdapter = AppCardAdapter(this)

    private var helper:ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navigationView:NavigationView = requireActivity().findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.recyclerHome.layoutManager = LinearLayoutManager(context)
        view.recyclerHome.adapter = adapter

        //activity!!.collapsingToolbarLayout.apply {
        //    setBackgroundColor(ContextCompat.getColor(context, R.color.colorComplementaryGreen))
        //}
        //activity!!.window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorComplementaryGreen)

        //toDo: observer runs async?
        val array = SingletonStatic.db!!.appDao().getAllApp()

        array.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            list = null
            list = it.sortedBy { appModel ->
                appModel.position
            }

            if(list!!.isEmpty()){
                view.empty_home1.visibility = View.VISIBLE
            }
            else{
                view.empty_home1.visibility = View.GONE
            }

            adapter.submitList(list)

            if(helper != null){
                helper!!.attachToRecyclerView(null)
                helper = null
            }

            helper = ItemTouchHelper(DragAndDropHelper(adapter, list!!))
            helper!!.attachToRecyclerView(view.recyclerHome)
        })

        requireActivity().drawer_layout.closeDrawers()

        // Inflate the layout for this fragment
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }*/

    override fun onPause() {
        list?.forEach {
            SingletonStatic.db!!.appDao().updateApp(it)
        }

        super.onPause()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        requireActivity().drawer_layout.closeDrawers()

        if(data != null && resultCode == 1){
            val packageName = data.extras!!["packageName"] as String
            val userName = data.extras!!["userName"] as String
            val applicationLabel:String = data.extras!!["applicationLabel"] as String

            val model = AppModel(0,
                applicationLabel,
                MutableLiveData<String>(userName),
                requireActivity().packageManager.getApplicationIcon(packageName),
                packageName,
                MutableLiveData<Boolean>(false),
                list!!.size)
            SingletonStatic.db!!.appDao().insertApp(model)
        }
    }

    private class DragAndDropHelper(val adapter: AppCardAdapter, var list: List<AppModel>): ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
            val positionDragged = viewHolder.adapterPosition //position of item selected to move
            val positionTarget = target.adapterPosition //position the selected item is currently at

            list[positionDragged].position = positionTarget
            list[positionTarget].position = positionDragged

            Collections.swap(list, positionDragged, positionTarget)

            //prevent scroll if already scrolled down a bit
            if(positionDragged == 0 || positionTarget == 0) {
                recyclerView.scrollToPosition(0)
            }

            //standard notify to update ui
            adapter.notifyItemMoved(positionDragged, positionTarget)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            GlobalScope.launch {
                SingletonStatic.db!!.appDao().deleteApp(list[viewHolder.adapterPosition])
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val f = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController:NavController = f.navController //for fragment switch

        when(p0.itemId){
            R.id.nav_home -> {
                navController.popBackStack()
                navController.navigate(R.id.homeFragment)
            }
            R.id.nav_select_game -> {
                requireActivity().drawer_layout.closeDrawers()

                val intent = Intent(this.context, AppSelectActivity::class.java)
                startActivityForResult(intent, 1)
            }
            R.id.lightTheme -> {
                val sharedPref = context?.getSharedPreferences(keyPreferenceTheme, Context.MODE_PRIVATE)
                val editor = sharedPref?.edit()
                editor?.putBoolean(keyTheme, false)
                editor?.apply()

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            R.id.darkTheme -> {
                val sharedPref = context?.getSharedPreferences(keyPreferenceTheme, Context.MODE_PRIVATE)
                val editor = sharedPref?.edit()
                editor?.putBoolean(keyTheme, true)
                editor?.apply()

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        return true
    }
}
