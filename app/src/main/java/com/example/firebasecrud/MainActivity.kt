package com.example.firebasecrud

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.firebasecrud.adapter.DataAdapter
import com.example.firebasecrud.databinding.ActivityMainBinding
import com.example.firebasecrud.databinding.AddDataBinding
import com.example.firebasecrud.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("users")



        binding.addData.setOnClickListener {
            val dilogBinding = AddDataBinding.inflate(layoutInflater)
            // Create the alert dialog builder.
            val builder = AlertDialog.Builder(this@MainActivity)

// Set the view on the dialog builder.
            builder.setView(dilogBinding.root)
//
            // Create and show the dialog.
//            val dialog = builder.create()
//            dialog.show()
//            val alertDialog = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Add")
//                .setView(view1)
                .setPositiveButton("Add", null)
//
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
            val dialogA = builder.create()
            dialogA.setOnShowListener {
                val addButton = dialogA.getButton(AlertDialog.BUTTON_POSITIVE)
                addButton.setOnClickListener {
                    val newName = dilogBinding.etvName.text.toString()
                    val newEmail = dilogBinding.etvEmail.text.toString()

                    if (newName.isEmpty()) {
                        dilogBinding.nameLayout.error = "This field is required!"
                    } else if (newEmail.isEmpty()) {
                        dilogBinding.emailLayout.error = "This field is required!"
                    } else {
                        val dialog = ProgressDialog(this@MainActivity)
                        dialog.setMessage("Storing in Database...")
                        dialog.show()

//                        val database = FirebaseDatabase.getInstance()
//                        val usersRef = database.reference.child("users")

                        val newUserId = myRef.push().key
                        val newUser = User(newUserId, newName, newEmail)

                        myRef.child(newUserId!!)
                            .setValue(newUser)
                            .addOnSuccessListener {
                                dialog.dismiss()
                                dialogA.dismiss() // Dismiss the dialog when saving is successful
                                Toast.makeText(this@MainActivity, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                dialog.dismiss()
                                Toast.makeText(this@MainActivity, "There was an error while saving data", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            dialogA.show()
        }



       myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val arrayList = ArrayList<User>()
                for (dataSnapshot in snapshot.children) {

                    val userdata = dataSnapshot.getValue(User::class.java)
//                    val id = snapshot.child("id").getValue(String::class.java)
//                    val name = snapshot.child("name").getValue(String::class.java)
//                    val email = snapshot.child("email").getValue(String::class.java)

//                    val userdata = User(id?: "", name?: "", email?: "")
//                    userdata?.id = dataSnapshot.key
                    userdata.let {
                        if (it != null) {
                            arrayList.add(it)
                        }
                    }
                }

                if (arrayList.isEmpty()) {
                    binding.tvHello.visibility = View.VISIBLE
                    binding.recycler.visibility = View.GONE
                } else {
                    binding.tvHello.visibility = View.GONE
                    binding.recycler.visibility = View.VISIBLE
                }

                val adapter = DataAdapter(this@MainActivity, arrayList)
                binding.recycler.adapter = adapter



                adapter.setOnItemClickListener(object : DataAdapter.OnItemClickListener {
                    override fun onClick(data: User) {
                        showEditDeletDalog(data)
                    }

                    private fun showEditDeletDalog(data: User) {
                        val view =
                            LayoutInflater.from(this@MainActivity).inflate(R.layout.add_data, null)
                        val nameLayout: TextInputLayout = view.findViewById(R.id.nameLayout)
                        val emailLayout: TextInputLayout = view.findViewById(R.id.email_layout)
                        val name: TextInputEditText = view.findViewById(R.id.etv_name)
                        val email: TextInputEditText = view.findViewById(R.id.etv_email)

                        name.setText(data.name)
                        email.setText(data.email)

                        val progressDialog = ProgressDialog(this@MainActivity)

                        val alertDialog = AlertDialog.Builder(this@MainActivity)
                            .setTitle("Edit")
                            .setView(view)
                            .setPositiveButton("Save") { dialogInterface, _ ->
                                val newName = name.text.toString()
                                val newEmail = email.text.toString()

                                if (newName.isEmpty()) {
                                    nameLayout.error = "This field is required!"
                                } else if (newEmail.isEmpty()) {
                                    emailLayout.error = "This field is required!"
                                } else {
                                    progressDialog.setMessage("Saving...")
                                    progressDialog.show()

                                    val updatedData = User(data.id!!, newName, newEmail)
                    //                                    val database = FirebaseDatabase.getInstance()
                    //                                    val usersRef = database.reference.child("users")

                                    myRef.child(data.id)
                                        .setValue(updatedData)
                                        .addOnSuccessListener {
                                            progressDialog.dismiss()
                                            dialogInterface.dismiss()
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Saved Successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                this@MainActivity,
                                                "There was an error while saving data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                    //
                            .setNeutralButton("Close") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .setNegativeButton("Delete") { dialogInterface, _ ->
                                progressDialog.setTitle("Deleting...")
                                progressDialog.show()
                                Log.d("data", data.id!!)
                                database.reference.child("users").child(data.id!!)
                                    .removeValue()
                                    .addOnSuccessListener {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Deleted Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        progressDialog.dismiss()
                                    }


                    //
                            }
                            .create()

                        alertDialog.show()
                    }
                })



            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })









    }



}