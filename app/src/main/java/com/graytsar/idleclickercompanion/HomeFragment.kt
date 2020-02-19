package com.graytsar.idleclickercompanion

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*
import kotlin.collections.ArrayList


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

    private val listAppCard = ArrayList<AppCardModel>()
    private lateinit var appCardAdapter:AppCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val navigationView:NavigationView = activity!!.findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_home, container, false)
        val linearLayoutManager = LinearLayoutManager(context)
        appCardAdapter = AppCardAdapter(context!!, listAppCard)

        view.recyclerHome.layoutManager = linearLayoutManager
        view.recyclerHome.adapter = appCardAdapter

        val helper = ItemTouchHelper(DragAndDropHelper(appCardAdapter, listAppCard))
        helper.attachToRecyclerView(view.recyclerHome)

        //test
        val pm = activity!!.packageManager
        val listPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in listPackages) {

            if(applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0){
                val label = pm.getApplicationLabel(pm.getApplicationInfo(applicationInfo.packageName, 0)).toString()
                val draw = pm.getApplicationIcon(pm.getApplicationInfo(applicationInfo.packageName, 0))
                val bmap = pm.getApplicationIcon(pm.getApplicationInfo(applicationInfo.packageName, 0)).toBitmap()
                val p = Palette.from(bmap).generate()
                val c1 = p.getDominantColor(0xFFFFFF)
                val c2 = p.getMutedColor(0xFFFFFF)

                val colorTitle = if(186.0 < (0.2126 * Color.red(c1) + 0.7152 * Color.green(c1) + 0.0722 * Color.blue(c1))){
                    0x000000
                } else{
                    0xFFFFFF
                }

                val colorText = if(186.0 < (0.2126 * Color.red(c2) + 0.7152 * Color.green(c2) + 0.0722 * Color.blue(c2))){
                    0x000000
                } else{
                    0xFFFFFF
                }
                listAppCard.add( AppCardModel(activity!!, label, "SavarKeiner", draw, applicationInfo.packageName))
            }
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        listAppCard.clear()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity!!.drawer_layout.closeDrawers()

        if(data != null && resultCode == 1){
            val packageName = data.extras!!["packageName"] as String
            val userName = data.extras!!["userName"] as String
            val appName:String = data.extras!!["appName"] as String

            val pm = activity!!.packageManager
            val drawable = pm.getApplicationIcon(packageName)

            val bmap = drawable.toBitmap()
            val p = Palette.from(bmap).generate()
            val c1 = p.getDominantColor(0xFFFFFF)
            val c2 = p.getMutedColor(0xFFFFFF)

            val colorTitle = if(186.0 < (0.2126 * Color.red(c1) + 0.7152 * Color.green(c1) + 0.0722 * Color.blue(c1))){
                0x000000
            } else{
                0xFFFFFF
            }

            val colorText = if(186.0 < (0.2126 * Color.red(c2) + 0.7152 * Color.green(c2) + 0.0722 * Color.blue(c2))){
                0x000000
            } else{
                0xFFFFFF
            }


            val gameCardModel = AppCardModel(activity!!, appName, userName, drawable, packageName)


            if(listAppCard.contains(gameCardModel)){
                return
            }

            listAppCard.add(0, gameCardModel)
            appCardAdapter.notifyDataSetChanged()
        }
    }

    private class DragAndDropHelper(val adapter: AppCardAdapter, val list: java.util.ArrayList<AppCardModel>): ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
            val positionDragged = viewHolder.adapterPosition //position of item selected to move
            val positionTarget = target.adapterPosition //position the selected item is currently at

            Collections.swap(list, positionDragged, positionTarget)

            //prevent scroll if already scrolled down a bit
            if(positionDragged == 0 || positionTarget == 0) {
                recyclerView.scrollToPosition(0)
            }

            //standard notify to update ui
            recyclerView.adapter!!.notifyItemMoved(positionDragged, positionTarget)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.list.removeAt(viewHolder.adapterPosition)
            adapter.notifyDataSetChanged()
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
        when(p0.itemId){
            R.id.nav_select_game -> {
                val intent = Intent(this.context, AppSelectActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }
        return true
    }
}
