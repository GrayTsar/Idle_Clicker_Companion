package com.graytsar.idleclickercompanion

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_app_detail.view.*
import kotlinx.android.synthetic.main.picker_alarm.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import kotlin.collections.ArrayList


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

    val list = ArrayList<AppAlarmModel>()
    lateinit var adapter:AppAlarmAdapter
    lateinit var serializable:SerializableAppDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        serializable = Gson().fromJson(arguments?.getString("obj"), SerializableAppDetail::class.java)
        //BitmapDrawable(context!!.resources, model.icon)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_detail, container, false)
        val linearLayoutManager = LinearLayoutManager(context)
        adapter = AppAlarmAdapter(this, list)

        view.recyclerAppDetail.layoutManager = linearLayoutManager
        view.recyclerAppDetail.adapter = adapter

        val helper = ItemTouchHelper(DragAndDropHelper(adapter, list))
        helper.attachToRecyclerView(view.recyclerAppDetail)

        activity!!.toolbarBackdrop.setImageBitmap(serializable.icon)
        activity!!.collapsingToolbarLayout.apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayTitle))
            //setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
            setCollapsedTitleTextColor(Color.WHITE)
            setExpandedTitleColor(Color.WHITE)
            title = serializable.appName
        }
        activity!!.window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorGrayDark)

        val dividerItemDecoration = DividerItemDecoration(
            view.recyclerAppDetail.context,
            linearLayoutManager.orientation
        )
        view.recyclerAppDetail.addItemDecoration(dividerItemDecoration)

        //gc does clean up
        activity!!.fab.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val infalter = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.picker_alarm, null)
            view.timePicker.setIs24HourView(true)
            view.timePicker.hour = 1
            view.timePicker.minute = 0

            builder.setView(view)
            builder.create()
            val dialog = builder.show()

            view.dialogPositiveButton.setOnClickListener{

                if(view.textPickerDescription.text.isEmpty()){
                    view.textPickerDescription.error = "Field cannot be blank"
                } else if(view.textPickerRepeat.text.isEmpty()){
                    view.textPickerRepeat.error = "Field cannot be blank"
                } else{
                    val item = AppAlarmModel(activity!!, serializable.appName, serializable.appPath, view.timePicker.hour, view.timePicker.minute, view.textPickerDescription.text.toString(), Integer.parseInt(view.textPickerRepeat.text.toString()))
                    list.add(item)
                    adapter.notifyDataSetChanged()
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.findViewById<ImageView>(R.id.toolbarBackdrop).setImageResource(0)
        activity!!.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbarLayout).setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
    }

    private class DragAndDropHelper(val adapter: AppAlarmAdapter, val list:ArrayList<AppAlarmModel>): ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
            var positionDragged = viewHolder.adapterPosition //position of item selected to move
            var positionTarget = target.adapterPosition //position the selected item is currently at

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
