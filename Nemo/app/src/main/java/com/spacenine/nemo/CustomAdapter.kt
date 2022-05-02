package com.spacenine.nemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.spacenine.nemo.bglocation.UserLocationActivity
import com.spacenine.nemo.util.checkForPhoneNumber


class CustomAdapter(context: Activity, private val mIntent: Intent, var contacts: MutableList<ContactModel?>) :
    ArrayAdapter<ContactModel?>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // create a database helper object
        // to handle the database manipulations
        var convertView = convertView
        val db = DbHelper(context)

        // Get the data item for this position
        val c = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val linearLayout = convertView!!.findViewById<View>(R.id.linear) as LinearLayout

        // Lookup view for data population
        val tvName = convertView.findViewById<TextView>(R.id.tvName)
        val tvPhone = convertView.findViewById<TextView>(R.id.tvPhone)
        val locationView = convertView.findViewById<ImageView>(R.id.locationView)

        locationView.setOnClickListener {

            val successListener = {

                val intent = mIntent.apply {
                    putExtra(UserLocationActivity.contactNameKey, tvName.text.toString())
                    putExtra(UserLocationActivity.phoneKey, tvPhone.text.toString())
                }

                context.startActivity(intent)

            }

            checkForPhoneNumber(tvPhone.text.toString(), successListener) {

                MaterialAlertDialogBuilder(context)
                    .setTitle("Location not found")
                    .setMessage("The requested user must install NEMO to track his location")
                    .setPositiveButton("OK") { dialogInterface, i ->
                    }.show()

            }

        }

        // Populate the data into the template
        // view using the data object
        tvName.text = c!!.name
        tvPhone.text = c.phoneNo

        linearLayout.setOnLongClickListener { // generate an MaterialAlertDialog Box
            MaterialAlertDialogBuilder(context)
                .setTitle("Remove Contact")
                .setMessage("Are you sure want to remove this contact?")
                .setPositiveButton(
                    "YES"
                ) { dialogInterface, i -> // delete the specified contact from the database
                    db.deleteContact(c)
                    // remove the item from the list
                    contacts.remove(c)
                    // notify the listview that dataset has been changed
                    notifyDataSetChanged()
                    Toast.makeText(context, "Contact removed!", Toast.LENGTH_SHORT).show()

                    if (db.count() == 0)
                        (context as Activity).findViewById<View>(R.id.noContactBanner).visibility = View.VISIBLE

                }
                .setNegativeButton(
                    "NO"
                ) { dialogInterface, i -> }
                .show()
            false
        }
        // Return the completed view to render on screen
        return convertView
    }

    // this method will update the ListView
    fun refresh(list: List<ContactModel?>?) {
        contacts.clear()
        contacts.addAll(list!!)
        notifyDataSetChanged()
    }

    fun clearAll() {
        contacts.clear()
        notifyDataSetChanged()
    }
}