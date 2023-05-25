package com.hieupm.btlandroid.views.adapters

import android.app.AlertDialog
import android.content.ClipData.Item
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hieupm.btlandroid.R
import com.hieupm.btlandroid.common.Constants
import com.hieupm.btlandroid.database.FirebaseDatabaseHelper
import com.hieupm.btlandroid.database.FirebaseRealtimeHelper
import com.hieupm.btlandroid.model.Exercise
import com.squareup.picasso.Picasso

class ItemExerciseAdapter (private var exerciseList: List<Exercise>) : RecyclerView.Adapter<ItemExerciseAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exerciseList[position]
        holder.itemView.setOnClickListener {
            showItemDetailDialog(holder.itemView.context, exercise)
        }
        holder.bind(exercise)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        private val imgExercise: ImageView = itemView.findViewById(R.id.imgExercise)

        fun bind(exercise: Exercise) {
            tvTitle.text = exercise.title
            tvCount.text = "x"+exercise.set_num.toString()
            Glide.with(itemView).asGif().load(exercise.uri_img).into(imgExercise)
        }
    }

    private fun showItemDetailDialog(context: Context, item: Exercise) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_item_exercise_detail, null)
        // Tạo và hiển thị dialog
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
        val dialog = builder.create()
        // Hiển thị thông tin chi tiết của item trong dialog
        val tvId = dialogView.findViewById<TextView>(R.id.tvId)
        val tvExName = dialogView.findViewById<TextView>(R.id.tvExName)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tvDesc)
        val tvSetNum = dialogView.findViewById<TextView>(R.id.tvSetNum)
        val tvSetTime = dialogView.findViewById<TextView>(R.id.tvSetTime)
        val imgDialog = dialogView.findViewById<ImageView>(R.id.imgDialog)
        var spLevel = dialogView.findViewById<Spinner>(R.id.spUpdate)
        val adapterLevel = ArrayAdapter<String>(dialogView.context,android.R.layout.simple_spinner_item,dialogView.resources.getStringArray(R.array.levels))
        adapterLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLevel.adapter = adapterLevel

        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)

        tvId.text = item.id
        tvExName.text = item.title
        tvDesc.text = item.description
        tvSetNum.text = item.set_num.toString()
        tvSetTime.text = item.set_time.toString()
        Glide.with(dialogView).asGif().load(item.uri_img).into(imgDialog)
        if(item.level_id.equals("beginner")){
            spLevel.setSelection(0)
        }else if(item.level_id.equals("intermediate")){
            spLevel.setSelection(1)
        }else if(item.level_id.equals("advanced")){
            spLevel.setSelection(2)
        }


        btnUpdate.setOnClickListener {
            val id = tvId.text.toString().trim()
            val title = tvExName.text.toString().trim()
            val desc = tvDesc.text.toString().trim()
            val num = tvSetNum.text.toString().trim().toInt()
            val time = tvSetTime.text.toString().trim().toInt()
            val level = dialogView.resources.getStringArray(R.array.levels)[spLevel.selectedItemPosition].trim().lowercase()
            if(id!=null && id.equals("")==false){
                val path = Constants.EXERCISE+id
                val exercise_update = Exercise(id,title,num,time,desc,item.uri_img,level)
                FirebaseDatabaseHelper.updateObject(path,exercise_update)
                Toast.makeText(context,"Update Success !",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Update Fail !",Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            val id_delete = tvId.text.toString().trim()
            AlertDialog.Builder(context)
                .setTitle("Delete ?")
                .setMessage("Are you sure?")
                .setPositiveButton("OK",DialogInterface.OnClickListener { dialogInterface, i ->
                    val path = Constants.EXERCISE+id_delete
                    FirebaseDatabaseHelper.deleteObject(path)
                    Toast.makeText(context,"Delete Success !",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                })
                .setNegativeButton("Cancel",DialogInterface.OnClickListener { dialogInterface, i ->
                    dialog.dismiss()
                })
                .show()
        }
        dialog.show()
    }

    public fun setData(new_data: List<Exercise>){
        this.exerciseList = new_data
        notifyDataSetChanged()
    }
}