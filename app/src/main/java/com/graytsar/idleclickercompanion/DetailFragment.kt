package com.graytsar.idleclickercompanion

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.picker_alarm.view.*
import kotlinx.android.synthetic.main.toolbar.*
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
 * [AppDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AppDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var list:List<AlarmModel>? = null
    private var adapter:AppAlarmAdapter = AppAlarmAdapter(this)
    private lateinit var model:AppModel

    private var helper:ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        model = SingletonStatic.db!!.appDao().findApp(arguments?.getLong("key")!!)[0]
        model.icon = context!!.packageManager.getApplicationIcon(model.packageName).toBitmap()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        view.recyclerAppDetail.layoutManager = LinearLayoutManager(context)
        view.recyclerAppDetail.adapter = adapter

        val array = SingletonStatic.db!!.alarmDao().getAllAlarm(model.idApp)
        array.observe(viewLifecycleOwner, androidx.lifecycle.Observer {it ->
            list = null
            list = it.sortedBy { alarmModel ->
                alarmModel.position
            }

            if(list!!.isEmpty()){
                view.empty_home2.visibility = View.VISIBLE
            }
            else{
                view.empty_home2.visibility = View.GONE
            }

            adapter.submitList(list)

            if(helper != null){
                helper!!.attachToRecyclerView(null)
                helper = null
            }

            helper = ItemTouchHelper(DragAndDropHelper(adapter, list!!))
            helper!!.attachToRecyclerView(view.recyclerAppDetail)
        })

        activity!!.toolbarBackdrop.setImageBitmap(model.icon)
        activity!!.collapsingToolbarLayout.apply {
            title = model.userName!!.value
        }
        //activity!!.window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorComplementaryGreen)

        //view.recyclerAppDetail.addItemDecoration(DividerItemDecoration(view.recyclerAppDetail.context, linearLayoutManager.orientation))

        //gc does clean up
        activity!!.fab.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val picker = inflater.inflate(R.layout.picker_alarm, null)
            picker.timePicker.setIs24HourView(true)

            if(Build.VERSION.SDK_INT < 23){
                picker.timePicker.currentHour = 1
                picker.timePicker.currentMinute = 0
            } else {
                picker.timePicker.hour = 1
                picker.timePicker.minute = 0
            }


            builder.setView(picker)
            builder.create()
            val dialog = builder.show()

            picker.dialogButtonDetail.setOnClickListener{

                if(picker.textPickerDetail.text.isEmpty()){
                    picker.textPickerDetail.error = getString(R.string.textPickerErrorHint)
                } else {
                    var hour = 1
                    var min = 0

                    if(Build.VERSION.SDK_INT < 23){
                        hour = picker.timePicker.currentHour
                        min = picker.timePicker.currentMinute
                    } else {
                        hour = picker.timePicker.hour
                        min = picker.timePicker.minute
                    }

                    val action = picker.textPickerDetail.text.toString()

                    val item = AlarmModel(0, model.idApp, model.applicationLabel, model.packageName,
                        hour, min, action, 0, MutableLiveData<Boolean>(false),
                        arrayOf(true,true,true,true,true,true,true), list!!.size)

                    GlobalScope.launch {
                        SingletonStatic.db!!.alarmDao().insertAlarm(item)
                    }

                    dialog.dismiss()
                }
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

    override fun onStop() {
        var alwaysTrue = true
        var atLeastOneTrue = false

        if(list != null){
            list!!.forEach{
                SingletonStatic.db!!.alarmDao().updateAlarm(it)
                if(it.startAlarm!!.value!! == false){
                    alwaysTrue = false
                } else {
                    atLeastOneTrue = true
                }
            }
        }

        if(atLeastOneTrue){
            model.startAll!!.value = true
            SingletonStatic.db!!.appDao().updateApp(model)
        }
        else {
            model.startAll!!.value = false
            SingletonStatic.db!!.appDao().updateApp(model)
        }

        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.findViewById<ImageView>(R.id.toolbarBackdrop).setImageResource(0)
        //activity!!.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout).setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private class DragAndDropHelper(val adapter: AppAlarmAdapter, val list:List<AlarmModel>): ItemTouchHelper.SimpleCallback(
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
                SingletonStatic.db!!.alarmDao().deleteAlarm(list[viewHolder.adapterPosition])
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
         * @return A new instance of fragment AppDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
