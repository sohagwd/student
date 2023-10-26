package com.fery.studentapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity(), DataAdapter.ItemClickListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val db = FirebaseFirestore.getInstance()
    private val dataCollection = db.collection("data")
    private val data = mutableListOf<Data>()
    private lateinit var adapter: DataAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = DataAdapter(data, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addBtn.setOnClickListener {
            val name = binding.nameEtxt.text.toString()
            val email = binding.emailEtxt.text.toString()
            val subject = binding.subEtxt.text.toString()
            val birthdate = binding.birthdateEtxt.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && subject.isNotEmpty() && birthdate.isNotEmpty()) {
                addData(name, email, subject, birthdate)
            }
        }
        fetchData()

    }

    private fun fetchData() {
        dataCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                data.clear()
                for(document in it){
                    val item = document.toObject(Data::class.java)
                    item.id = document.id
                    data.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data fetched failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addData(
        title: String,
        description: String,
        subject: LinearLayoutManager,
        birthdate: LinearLayoutManager
    ) {
        val newData = Data(title = title, description = description, timestamp = Timestamp.now())
        dataCollection.add(newData)
            .addOnSuccessListener {
                newData.id = it.id
                data.add(newData)
                adapter.notifyDataSetChanged()
                binding.titleEtxt.text?.clear()
                binding.descEtxt.text?.clear()
                fetchData()
                Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data added failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onEditItemClick(data: Data) {
        binding.titleEtxt.setText(data.title)
        binding.descEtxt.setText(data.description)
        binding.addBtn.text = "Update"

        binding.addBtn.setOnClickListener {
            val updateTitle = binding.titleEtxt.text.toString()
            val updateDescription = binding.descEtxt.text.toString()

            if(updateTitle.isNotEmpty() && updateDescription.isNotEmpty()){
                val updateData = Data(data.id, updateTitle, updateDescription,Timestamp.now())

                dataCollection.document(data.id!!)
                    .set(updateData)
                    .addOnSuccessListener {
                        binding.titleEtxt.text?.clear()
                        binding.descEtxt.text?.clear()
                        Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data updated failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onDeleteItemClick(data: Data) {
        dataCollection.document(data.id!!)
            .delete()
            .addOnSuccessListener {
                adapter.notifyDataSetChanged()
                fetchData()
                Toast.makeText(this, "Data deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data deletion failed", Toast.LENGTH_SHORT).show()
            }
    }
}